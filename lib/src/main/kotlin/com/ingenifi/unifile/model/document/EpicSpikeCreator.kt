package com.ingenifi.unifile.model.document

import com.ingenifi.unifile.KeywordExtractor
import com.ingenifi.unifile.VerbosePrinter
import com.ingenifi.unifile.VerbosePrinting
import com.ingenifi.unifile.Verbosity

data class EpicSpikeCreator(val spike: EpicSpike, val keywordExtractor: KeywordExtractor, val verbosity: Verbosity) : SectionCreator, VerbosePrinting by VerbosePrinter(verbosity) {
    override fun create(sectionNumber: SectionNumber): List<Section> {
        val headingName = Name("Jira Epic Child Spike")
        val title = Title(spike.title)
        val heading = Heading(headingName = headingName, sectionNumber = sectionNumber, title = title)
        val keywords = createKeywords()
        val detail = DetailText.Detail(spike.detail)
        val summary = createSummary()
        val text = UnifileBodyText(headingName, keywords = keywords, detail = detail, summary = summary)
        val section = Section(heading = heading, text = text)
        return listOf(section)
    }

    private fun createSummary(): SummaryText {
        val content = "This is a child spike '${spike.key} - ${spike.title}' of epic '${spike.epic.key} - ${spike.epic.title}"
        return SummaryText.Summary(content = content)
    }

    private fun createKeywords(): KeywordsText.Keywords {
        val detailKeywords = keywordExtractor.extract(spike.detail)
        val additionalKeywords = mutableListOf<String>()
        additionalKeywords.add("child")
        additionalKeywords.add("spike")
        additionalKeywords.add(spike.epic.key)
        additionalKeywords.add(spike.key)
        val keywords = detailKeywords + additionalKeywords
        verbosePrint("epic spike keywords are $keywords")
        return KeywordsText.Keywords(keywords)
    }
}