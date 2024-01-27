package com.ingenifi.unifile.model.document

import com.ingenifi.unifile.KeywordExtractor
import com.ingenifi.unifile.VerbosePrinter
import com.ingenifi.unifile.VerbosePrinting
import com.ingenifi.unifile.Verbosity
import com.ingenifi.unifile.formatter.jira.JiraApi

data class EpicCreator(val epic: Epic, val keywordExtractor: KeywordExtractor, val api: JiraApi, val issueCreator: IssueCreator, val verbosity: Verbosity) : SectionCreator,
    VerbosePrinting by VerbosePrinter(verbosity) {
    override fun create(sectionNumber: SectionNumber): List<Section> {
        var childNumberCounter = 1
        val childrenSections = api.getChildren(epic.key).map {
            issueCreator.create(it, epic = epic)
        }.map {
            val config = SectionCreatorConfig(
                jiraIssue = it, keywordExtractor = keywordExtractor, api = api, number = sectionNumber.append(childNumberCounter++), issueCreator = issueCreator, verbosity = verbosity
            )
            SectionCreator.create(config)
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