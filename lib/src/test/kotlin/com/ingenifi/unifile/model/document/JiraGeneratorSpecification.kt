package com.ingenifi.unifile.model.document

import com.ingenifi.unifile.*
import com.ingenifi.unifile.formatter.jira.IssueFactory
import com.ingenifi.unifile.formatter.jira.JiraApi
import com.ingenifi.unifile.model.document.SectionGenerator.Config
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import java.io.File

class JiraGeneratorSpecification : StringSpec({
    val config = Config(keywordExtractor = KeywordExtractor(), verbosity = Verbosity(verbose = true, level = 0), parameterStore = ParameterStore.loadProperties())
    fun generate(jiraType: String) = JiraGenerator(config = config, number = 1, file = resourceAsFile(name = jiraType, extension = "jira")).generate()
    fun validate(jiraType: String, sections: List<Section>, output: Boolean = false) {
        val actual = DocumentGenerator(Document(sections = sections)).generate()
        val expected = resourceAsString("expected-${jiraType}-document.txt")
        if (output) {
            actual.output("actual")
            expected.output("expected")
        }
        actual shouldBe expected
    }

    "generate story" {
        validate("story", generate("story"))
    }

    "generate spike" {
        validate("spike", generate("spike"))
    }

    "generate bug" {
        validate("bug", generate("bug"))
    }

    "generate epic".config(enabled = true) {
        validate("epic", generate("epic"), output = true)
    }
})


data class JiraGenerator(val config: Config, val number: Int, val file: File) : SectionGenerator {
    private val api = JiraApi(config.client, config.parameterStore.getParameter("jiraBaseUrl"), config.parameterStore.getParameter("apiToken"))
    private val issueCreator = IssueCreator(api)
    override fun generate(): List<Section> = runBlocking {

        file.readLines().map { issueCreator.create(it) }.map {
            val sectionCreatorConfig = SectionCreator.SectionCreatorConfig(jiraIssue = it, keywordExtractor = config.keywordExtractor, api = api, issueCreator = issueCreator, number = SectionNumber(number), verbosity = config.verbosity)
            SectionCreator.create(sectionCreatorConfig)
        }.flatten()
    }


}

data class IssueCreator(val api: JiraApi) {
    fun create(issueKey: String, epic : Epic? = null): JiraIssue {
        val issueData = IssueData.from(issueKey, api.getIssue(issueKey))
        return when (issueData.type) {
            "epic" -> JiraIssue.epic(issueData)
            "story" -> if (epic != null) JiraIssue.epicStory(issueData, epic) else JiraIssue.story(issueData)
            "spike" -> if (epic != null) JiraIssue.epicSpike(issueData, epic) else JiraIssue.spike(issueData)
            "bug" -> if (epic != null) JiraIssue.epicBug(issueData, epic) else JiraIssue.bug(issueData)
            else -> throw IllegalArgumentException("type '${issueData.type}' not yet supported")
        }
    }
}

sealed interface SectionCreator  {
    data class SectionCreatorConfig(val jiraIssue: JiraIssue, val keywordExtractor: KeywordExtractor, val api: JiraApi, val number: SectionNumber, val issueCreator: IssueCreator, val verbosity: Verbosity)
    companion object {
        fun create(config : SectionCreatorConfig): List<Section> = when (config.jiraIssue) {
            is Epic -> EpicCreator(epic = config.jiraIssue, keywordExtractor = config.keywordExtractor, api = config.api, issueCreator = config.issueCreator, verbosity = config.verbosity).create(config.number)
            is Story -> StoryCreator(story = config.jiraIssue, keywordExtractor = config.keywordExtractor).create(config.number)
            is Spike -> SpikeCreator(spike = config.jiraIssue, keywordExtractor = config.keywordExtractor).create(config.number)
            is Bug -> BugCreator(bug = config.jiraIssue, keywordExtractor = config.keywordExtractor).create(config.number)
            is EpicStory -> EpicStoryCreator(story = config.jiraIssue, keywordExtractor = config.keywordExtractor, verbosity = config.verbosity).create(config.number)
            is EpicSpike -> EpicSpikeCreator(spike = config.jiraIssue, keywordExtractor = config.keywordExtractor, verbosity = config.verbosity).create(config.number)
            is EpicBug -> EpicBugCreator(bug = config.jiraIssue, keywordExtractor = config.keywordExtractor).create(config.number)
            else -> throw IllegalArgumentException("type not yet supported")
        }
    }

    fun create(sectionNumber: SectionNumber): List<Section>

    data class StoryCreator(val story: Story, val keywordExtractor: KeywordExtractor) : SectionCreator {
        override fun create(sectionNumber: SectionNumber): List<Section> {
            val headingName = Name("Jira Story")
            val title = Title(story.title)
            val heading = Heading(headingName = headingName, sectionNumber = sectionNumber, title = title)
            val keywords = KeywordsText.Keywords(keywordExtractor.extract(story.detail))
            val text = UnifileBodyText(headingName, keywords = keywords, detail = DetailText.Detail(story.detail))
            val section = Section(heading = heading, text = text)
            return listOf(section)
        }
    }

    data class EpicCreator(val epic: Epic, val keywordExtractor: KeywordExtractor, val api: JiraApi, val issueCreator: IssueCreator, val verbosity: Verbosity) : SectionCreator, VerbosePrinting by VerbosePrinter(verbosity) {
        override fun create(sectionNumber: SectionNumber): List<Section> {
            var childNumberCounter = 1
            val childrenSections = api.getChildren(epic.key).map {
                issueCreator.create(it, epic = epic)
            }.map {
                val config = SectionCreatorConfig(jiraIssue = it, keywordExtractor = keywordExtractor, api = api, number = sectionNumber.append(childNumberCounter++), issueCreator = issueCreator, verbosity = verbosity)
                create(config)
            }.flatten()

            val epicSection = createEpicSection(sectionNumber)
            return childrenSections + epicSection
        }

        private fun createEpicSection(sectionNumber: SectionNumber): List<Section> {
            val headingName = Name("Jira Epic")
            val title = Title(epic.title)
            val heading = Heading(headingName = headingName, sectionNumber = sectionNumber, title = title)
            val keywords = KeywordsText.Keywords(keywordExtractor.extract(epic.detail))
            val detail = DetailText.Detail(epic.detail)
            val summary = SummaryText.Summary(epic.summary)
            val text = UnifileBodyText(headingName, keywords = keywords, detail = detail, summary = summary)
            val section = Section(heading = heading, text = text)
            return listOf(section)
        }
    }


    data class SpikeCreator(val spike: Spike, val keywordExtractor: KeywordExtractor) : SectionCreator {
        override fun create(sectionNumber: SectionNumber): List<Section> {
            val headingName = Name("Jira Spike")
            val title = Title(spike.title)
            val heading = Heading(headingName = headingName, sectionNumber = sectionNumber, title = title)
            val keywords = KeywordsText.Keywords(keywordExtractor.extract(spike.detail))
            val text = UnifileBodyText(headingName, keywords = keywords, detail = DetailText.Detail(spike.detail))
            val section = Section(heading = heading, text = text)
            return listOf(section)
        }
    }


    data class BugCreator(val bug: Bug, val keywordExtractor: KeywordExtractor) : SectionCreator {
        override fun create(sectionNumber: SectionNumber): List<Section> {
            val headingName = Name("Jira Bug")
            val title = Title(bug.title)
            val heading = Heading(headingName = headingName, sectionNumber = sectionNumber, title = title)
            val keywords = KeywordsText.Keywords(keywordExtractor.extract(bug.detail))
            val text = UnifileBodyText(headingName, keywords = keywords, detail = DetailText.Detail(bug.detail))
            val section = Section(heading = heading, text = text)
            return listOf(section)
        }
    }

    data class EpicStoryCreator(val story: EpicStory, val keywordExtractor: KeywordExtractor, val verbosity: Verbosity) : SectionCreator, VerbosePrinting by VerbosePrinter(verbosity) {
        override fun create(sectionNumber: SectionNumber): List<Section> {
            val headingName = Name("Jira Epic Child Story")
            val title = Title(story.title)
            val heading = Heading(headingName = headingName, sectionNumber = sectionNumber, title = title)
            val keywords = createKeywords()
            val detail = DetailText.Detail(story.detail)
            val summary = createSummary()
            val text = UnifileBodyText(headingName, keywords = keywords, detail = detail, summary = summary)
            val section = Section(heading = heading, text = text)
            return listOf(section)
        }

        private fun createSummary(): SummaryText {
            val content = "This is a child story '${story.key} - ${story.title}' of epic '${story.epic.key} - ${story.epic.title}"
            return SummaryText.Summary(content = content)
        }

        private fun createKeywords() : KeywordsText.Keywords {
            val detailKeywords = keywordExtractor.extract(story.detail)
            val additionalKeywords = mutableListOf<String>()
            additionalKeywords.add("child")
            additionalKeywords.add("story")
            additionalKeywords.add(story.epic.key)
            additionalKeywords.add(story.key)
            val keywords = detailKeywords + additionalKeywords
            verbosePrint("keywords are $keywords")
            return KeywordsText.Keywords(keywords)
        }
    }

}

data class EpicSpikeCreator(val spike: EpicSpike, val keywordExtractor: KeywordExtractor, val verbosity: Verbosity) : SectionCreator, VerbosePrinting by VerbosePrinter(verbosity) {
    override fun create(sectionNumber: SectionNumber): List<Section> {
        val headingName = Name("Jira Epic Child Spike")
        val title = Title(spike.title)
        val heading = Heading(headingName = headingName, sectionNumber = sectionNumber, title = title)
        val keywords = createKeywords()
        val detail = DetailText.Detail(spike.detail)
        val summary = createSummary()
        val text = UnifileBodyText(headingName, keywords = keywords, detail = detail, summary = summary)
        val section = Section(heading = heading, text = text)
        return listOf(section)
    }

    private fun createSummary(): SummaryText {
        val content = "This is a child spike '${spike.key} - ${spike.title}' of epic '${spike.epic.key} - ${spike.epic.title}"
        return SummaryText.Summary(content = content)
    }
    private fun createKeywords() : KeywordsText.Keywords {
        val detailKeywords = keywordExtractor.extract(spike.detail)
        val additionalKeywords = mutableListOf<String>()
        additionalKeywords.add("child")
        additionalKeywords.add("spike")
        additionalKeywords.add(spike.epic.key)
        additionalKeywords.add(spike.key)
        val keywords = detailKeywords + additionalKeywords
        verbosePrint("epic spike keywords are $keywords")
        return KeywordsText.Keywords(keywords)
    }
}

data class EpicBugCreator(val bug: EpicBug, val keywordExtractor: KeywordExtractor) : SectionCreator {
    override fun create(sectionNumber: SectionNumber): List<Section> {
        val headingName = Name("Jira Epic Child Spike")
        val title = Title(bug.title)
        val heading = Heading(headingName = headingName, sectionNumber = sectionNumber, title = title)
        val keywords = createKeywords()
        val detail = DetailText.Detail(bug.detail)
        val summary = createSummary()
        val text = UnifileBodyText(headingName, keywords = keywords, detail = detail, summary = summary)
        val section = Section(heading = heading, text = text)
        return listOf(section)
    }

    private fun createSummary(): SummaryText {
        val content = "This is a child bug '${bug.key} - ${bug.title}' of epic '${bug.epic.key} - ${bug.epic.title}"
        return SummaryText.Summary(content = content)
    }

    private fun createKeywords() : KeywordsText.Keywords {
        val detailKeywords = keywordExtractor.extract(bug.detail)
        val additionalKeywords = mutableListOf<String>()
        additionalKeywords.add("child")
        additionalKeywords.add("bug")
        additionalKeywords.add(bug.epic.key)
        additionalKeywords.add(bug.key)
        return KeywordsText.Keywords(detailKeywords)
    }
}



data class IssueData(private val rawMap: Map<String, Any>?, val key: String, val type: String, val detail: String) {
    fun getStoryTitle() = getNonEpicTitle()
    fun getSpikeTitle() = getNonEpicTitle()
    fun getBugTitle() = getNonEpicTitle()
    private fun getNonEpicTitle() = rawMap?.get("summary") as String
    fun getEpicTitle(): String = rawMap?.get("customfield_10304") as String
    fun getEpicSummary(): String = rawMap?.get("summary") as String

    companion object {
        fun from(key: String, rawMap: Map<String, Any>?): IssueData {
            return IssueData(rawMap = rawMap, key = key, type = parseType(rawMap), detail = rawMap?.get("description") as String)
        }

        private fun parseType(rawMap: Map<String, Any>?): String {
            val issueTypeMap = rawMap?.get(IssueFactory.ISSUE_TYPE) as Map<String, Any>
            val type = issueTypeMap["name"] as String
            return type.lowercase()
        }
    }
}

interface JiraIssue {
    val key: String
    val detail: String

    companion object {
        fun epic(issueData: IssueData): Epic = Epic(key = issueData.key, detail = issueData.detail, summary = issueData.getEpicSummary(), title = issueData.getEpicTitle())
        fun story(issueData: IssueData): Story = Story(key = issueData.key, detail = issueData.detail, title = issueData.getStoryTitle())
        fun spike(issueData: IssueData): Spike = Spike(key = issueData.key, detail = issueData.detail, title = issueData.getSpikeTitle())
        fun bug(issueData: IssueData): Bug = Bug(key = issueData.key, detail = issueData.detail, title = issueData.getBugTitle())
        fun epicStory(issueData: IssueData, epic : Epic): EpicStory = EpicStory(key = issueData.key, detail = issueData.detail, title = issueData.getBugTitle(), epic = epic)
        fun epicSpike(issueData: IssueData, epic : Epic): EpicSpike = EpicSpike(key = issueData.key, detail = issueData.detail, title = issueData.getBugTitle(), epic = epic)
        fun epicBug(issueData: IssueData, epic : Epic): EpicBug = EpicBug(key = issueData.key, detail = issueData.detail, title = issueData.getBugTitle(), epic = epic)

    }
}

data class Epic(override val key: String, override val detail: String, val summary: String, val title: String, val children: List<JiraIssue> = listOf()) : JiraIssue
data class Story(override val key: String, override val detail: String, val title: String) : JiraIssue
data class Spike(override val key: String, override val detail: String, val title: String) : JiraIssue
data class Bug(override val key: String, override val detail: String, val title: String) : JiraIssue
data class EpicStory(override val key: String, override val detail: String, val title: String, val epic : Epic) : JiraIssue
data class EpicSpike(override val key: String, override val detail: String, val title: String, val epic : Epic) : JiraIssue
data class EpicBug(override val key: String, override val detail: String, val title: String, val epic : Epic) : JiraIssue


