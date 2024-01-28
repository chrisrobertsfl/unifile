package com.ingenifi.unifile.model.generators.jira

import com.ingenifi.unifile.model.generators.KeywordExtractor
import com.ingenifi.unifile.verbosity.VerbosePrinter
import com.ingenifi.unifile.verbosity.VerbosePrinting
import com.ingenifi.unifile.verbosity.Verbosity
import com.ingenifi.unifile.model.document.*

data class EpicSpikeCreator(val spike: EpicSpike, val keywordExtractor: KeywordExtractor, val verbosity: Verbosity) : SectionCreator, VerbosePrinting by VerbosePrinter(verbosity) {
    override fun create(sectionNumber: SectionNumber): List<Section> {
        verbosePrint("Processing epic child spike '${spike.key} - ${spike.title}' from epic '${spike.epic.key} - ${spike.epic.title}'")
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
        return KeywordsText.Keywords(keywords)
    }
}