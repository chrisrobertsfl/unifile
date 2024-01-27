package com.ingenifi.unifile.model.document

import com.ingenifi.unifile.KeywordExtractor

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

    private fun createKeywords(): KeywordsText.Keywords {
        val detailKeywords = keywordExtractor.extract(bug.detail)
        val additionalKeywords = mutableListOf<String>()
        additionalKeywords.add("child")
        additionalKeywords.add("bug")
        additionalKeywords.add(bug.epic.key)
        additionalKeywords.add(bug.key)
        return KeywordsText.Keywords(detailKeywords)
    }
}