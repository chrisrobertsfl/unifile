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
    val config = Config(keywordExtractor = KeywordExtractor(), verbosity = mockk<Verbosity>(), parameterStore = ParameterStore.loadProperties())
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
})


data class JiraGenerator(val config: Config, val number: Int, val file: File) : SectionGenerator {
    private val api = JiraApi(config.client, config.parameterStore.getParameter("jiraBaseUrl"), config.parameterStore.getParameter("apiToken"))
    override fun generate(): List<Section> = runBlocking {
        file.readLines().map { createIssue(it) }.map {
            createSection(it)
        }.flatten()
    }

    private fun createIssue(issueKey: String): JiraIssue {
        val issueData = IssueData.from(issueKey, api.getIssue(issueKey))
        return when (issueData.type) {
            "story" -> JiraIssue.story(issueData)
            "spike" -> JiraIssue.spike(issueData)
            "bug" -> JiraIssue.bug(issueData)
            else -> throw IllegalArgumentException("type '${issueData.type}' not yet supported")
        }
    }

    private fun createSection(jiraIssue: JiraIssue): List<Section> = when (jiraIssue) {
        is Story -> SectionCreator.StoryCreator(story = jiraIssue, keywordExtractor = config.keywordExtractor).create(number)
        is Spike -> SectionCreator.SpikeCreator(spike = jiraIssue, keywordExtractor = config.keywordExtractor).create(number)
        is Bug -> SectionCreator.BugCreator(bug = jiraIssue, keywordExtractor = config.keywordExtractor).create(number)

        else -> throw IllegalArgumentException("type not yet supported")
    }
}

sealed interface SectionCreator {
    fun create(number: Int): List<Section>

    data class StoryCreator(val story: Story, val keywordExtractor: KeywordExtractor) : SectionCreator {
        override fun create(number: Int): List<Section> {
            val headingName = Name("Jira Story")
            val sectionNumber = SectionNumber(number)
            val title = Title(story.title)
            val heading = Heading(headingName = headingName, sectionNumber = sectionNumber, title = title)
            val keywords = KeywordsText.Keywords(keywordExtractor.extract(story.detail))
            val text = UnifileBodyText(headingName, keywords = keywords, detail = DetailText.Detail(story.detail))
            val section = Section(heading = heading, text = text)
            return listOf(section)
        }
    }

    data class SpikeCreator(val spike: Spike, val keywordExtractor: KeywordExtractor) : SectionCreator {
        override fun create(number: Int): List<Section> {
            val headingName = Name("Jira Spike")
            val sectionNumber = SectionNumber(number)
            val title = Title(spike.title)
            val heading = Heading(headingName = headingName, sectionNumber = sectionNumber, title = title)
            val keywords = KeywordsText.Keywords(keywordExtractor.extract(spike.detail))
            val text = UnifileBodyText(headingName, keywords = keywords, detail = DetailText.Detail(spike.detail))
            val section = Section(heading = heading, text = text)
            return listOf(section)
        }
    }


    data class BugCreator(val bug: Bug, val keywordExtractor: KeywordExtractor) : SectionCreator {
        override fun create(number: Int): List<Section> {
            val headingName = Name("Jira Spike")
            val sectionNumber = SectionNumber(number)
            val title = Title(bug.title)
            val heading = Heading(headingName = headingName, sectionNumber = sectionNumber, title = title)
            val keywords = KeywordsText.Keywords(keywordExtractor.extract(bug.detail))
            val text = UnifileBodyText(headingName, keywords = keywords, detail = DetailText.Detail(bug.detail))
            val section = Section(heading = heading, text = text)
            return listOf(section)
        }
    }
}

data class IssueData(private val rawMap: Map<String, Any>?, val key: String, val type: String, val detail: String) {
    fun getStoryTitle() = getNonEpicTitle()
    fun getSpikeTitle() = getNonEpicTitle()
    fun getBugTitle() = getNonEpicTitle()
    private fun getNonEpicTitle() = rawMap?.get("summary") as String

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
        fun story(issueData: IssueData): Story = Story(key = issueData.key, detail = issueData.detail, title = issueData.getStoryTitle())
        fun spike(issueData: IssueData): Spike = Spike(key = issueData.key, detail = issueData.detail, title = issueData.getSpikeTitle())
        fun bug(issueData: IssueData): Bug = Bug(key = issueData.key, detail = issueData.detail, title = issueData.getBugTitle())

    }
}

data class Story(override val key: String, override val detail: String, val title: String) : JiraIssue
data class Spike(override val key: String, override val detail: String, val title: String) : JiraIssue
data class Bug(override val key: String, override val detail: String, val title: String) : JiraIssue