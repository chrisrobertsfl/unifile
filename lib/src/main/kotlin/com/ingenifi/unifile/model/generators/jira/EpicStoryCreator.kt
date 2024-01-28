package com.ingenifi.unifile.model.generators.jira

import com.ingenifi.unifile.model.generators.KeywordExtractor
import com.ingenifi.unifile.verbosity.Verbosity
import com.ingenifi.unifile.model.document.*
import com.ingenifi.unifile.verbosity.VerbosePrinter
import com.ingenifi.unifile.verbosity.VerbosePrinting

data class EpicStoryCreator(
    private val story: EpicStory, private val keywordExtractor: KeywordExtractor, private val verbosity: Verbosity,
) : SectionCreator, VerbosePrinting by VerbosePrinter(verbosity) {
    private val childCreator: ChildCreator = DefaultChildCreator(story, keywordExtractor)

    override fun create(sectionNumber: SectionNumber): List<Section> {
        verbosePrint("Processing epic child story '${story.key} - ${story.title}' from epic '${story.epic.key} - ${story.epic.title}'")
        val headingName = Name("Jira Epic Child Story")
        val heading = Heading(headingName = headingName, sectionNumber = sectionNumber, title = TitleText.Title(story.title))
        val keywords = childCreator.createKeywords()
        val summary = childCreator.createSummary()
        val detail = DetailText.Detail(story.detail)
        val text = UnifileBodyText(headingName, keywords = keywords, detail = detail, summary = summary)
        return listOf(Section(heading = heading, text = text))
    }
}