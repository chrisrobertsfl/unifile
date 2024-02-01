package com.ingenifi.unifile.model.generators.jira

import com.ingenifi.unifile.model.document.*
import com.ingenifi.unifile.model.generators.KeywordExtractor
import com.ingenifi.unifile.VerbosePrinter
import com.ingenifi.unifile.VerbosePrinting
import com.ingenifi.unifile.Verbosity
import com.ingenifi.unifile.model.document.TitleText.Title

data class EpicCreator(val epic: Epic, val keywordExtractor: KeywordExtractor, val api: JiraApi, val issueCreator: IssueCreator, val verbosity: Verbosity) : SectionCreator,
    VerbosePrinting by VerbosePrinter(verbosity) {
    override fun create(sectionNumber: SectionNumber): List<Section> {
        verbosePrint("Processing Epic '${epic.reference()}'")
        val childVerbosity = verbosity.increasingBy(by = 1)
        var childNumberCounter = 1
        val childrenSections = api.getChildren(epic.key).map {
            issueCreator.createEpicChild(it, epic)
        }.map {
            val config = SectionCreatorConfig(
                jiraIssue = it, keywordExtractor = keywordExtractor, api = api, number = sectionNumber.append(childNumberCounter++), issueCreator = issueCreator, verbosity = childVerbosity
            )
            SectionCreator.create(config)
        }.flatten()

        val epicSection = createEpicSection(sectionNumber)
        return childrenSections + epicSection
    }

    private fun createEpicSection(sectionNumber: SectionNumber): List<Section> {
        val headingName = Name("Jira Epic")
        val title = Title(epic.reference())
        val heading = Heading(headingName = headingName, sectionNumber = sectionNumber, title = title)
        val keywords = createKeywords()
        val detail = DetailText.Detail(epic.detail)
        val summary = SummaryText.Summary(epic.summary)
        val text = UnifileBodyText(headingName, keywords = keywords, detail = detail, summary = summary)
        val section = Section(heading = heading, bodyText = text)
        return listOf(section)
    }

    private fun createKeywords() = KeywordsText.Keywords(keywordExtractor.extract(epic.detail) + epic.keywords)

    private fun Epic.reference(): String = "${key} - ${title}"

}