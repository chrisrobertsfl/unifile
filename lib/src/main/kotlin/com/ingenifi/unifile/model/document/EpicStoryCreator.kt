package com.ingenifi.unifile.model.document

import com.ingenifi.unifile.KeywordExtractor
import com.ingenifi.unifile.Verbosity

data class EpicStoryCreator(
    private val story: EpicStory, private val keywordExtractor: KeywordExtractor, private val verbosity: Verbosity,
) : SectionCreator {
    private val childCreator: ChildCreator = DefaultChildCreator(story, keywordExtractor)

    override fun create(sectionNumber: SectionNumber): List<Section> {
        val headingName = Name("Jira Epic Child Story")
        val heading = Heading(headingName = headingName, sectionNumber = sectionNumber, title = Title(story.title))
        val keywords = childCreator.createKeywords()
        val summary = childCreator.createSummary()
        val detail = DetailText.Detail(story.detail)
        val text = UnifileBodyText(headingName, keywords = keywords, detail = detail, summary = summary)
        return listOf(Section(heading = heading, text = text))
    }
}