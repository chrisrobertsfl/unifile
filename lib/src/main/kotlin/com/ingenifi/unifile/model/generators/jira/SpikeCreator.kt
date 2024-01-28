package com.ingenifi.unifile.model.generators.jira

import com.ingenifi.unifile.model.generators.KeywordExtractor
import com.ingenifi.unifile.model.document.*
import com.ingenifi.unifile.verbosity.VerbosePrinter
import com.ingenifi.unifile.verbosity.VerbosePrinting
import com.ingenifi.unifile.verbosity.Verbosity

data class SpikeCreator(val spike: Spike, val keywordExtractor: KeywordExtractor, val verbosity: Verbosity) : SectionCreator, VerbosePrinting by VerbosePrinter(verbosity) {
    override fun create(sectionNumber: SectionNumber): List<Section> {
        verbosePrint("Processing Story '${spike.key} - ${spike.title}'")
        val headingName = Name("Jira Spike")
        val title = Title(spike.title)
        val heading = Heading(headingName = headingName, sectionNumber = sectionNumber, title = title)
        val keywords = KeywordsText.Keywords(keywordExtractor.extract(spike.detail))
        val text = UnifileBodyText(headingName, keywords = keywords, detail = DetailText.Detail(spike.detail))
        val section = Section(heading = heading, text = text)
        return listOf(section)
    }
}