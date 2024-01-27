package com.ingenifi.unifile.model.generators.jira

import com.ingenifi.unifile.KeywordExtractor
import com.ingenifi.unifile.model.document.*

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