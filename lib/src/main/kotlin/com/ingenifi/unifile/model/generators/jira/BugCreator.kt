package com.ingenifi.unifile.model.generators.jira

import com.ingenifi.unifile.KeywordExtractor
import com.ingenifi.unifile.model.document.*

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