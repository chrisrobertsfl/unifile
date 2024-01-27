package com.ingenifi.unifile.model.generators.jira

import com.ingenifi.unifile.model.generators.KeywordExtractor
import com.ingenifi.unifile.model.document.*

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