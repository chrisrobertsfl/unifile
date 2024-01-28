package com.ingenifi.unifile.model.generators.jira

import com.ingenifi.unifile.model.generators.KeywordExtractor
import com.ingenifi.unifile.verbosity.VerbosePrinter
import com.ingenifi.unifile.verbosity.VerbosePrinting
import com.ingenifi.unifile.verbosity.Verbosity
import com.ingenifi.unifile.model.document.*

data class EpicCreator(val epic: Epic, val keywordExtractor: KeywordExtractor, val api: JiraApi, val issueCreator: IssueCreator, val verbosity: Verbosity) : SectionCreator,
    VerbosePrinting by VerbosePrinter(verbosity) {
    override fun create(sectionNumber: SectionNumber): List<Section> {
        verbosePrint("Processing Epic '${epic.key} - ${epic.title}'")
        val childVerbosity = verbosity.increasingBy(by = 1)
        var childNumberCounter = 1
        val childrenSections = api.getChildren(epic.key).map {
            issueCreator.create(it, epic = epic)
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
        val title = TitleText.Title(epic.title)
        val heading = Heading(headingName = headingName, sectionNumber = sectionNumber, title = title)
        val keywords = KeywordsText.Keywords(keywordExtractor.extract(epic.detail))
        val detail = DetailText.Detail(epic.detail)
        val summary = SummaryText.Summary(epic.summary)
        val text = UnifileBodyText(headingName, keywords = keywords, detail = detail, summary = summary)
        val section = Section(heading = heading, text = text)
        return listOf(section)
    }
}