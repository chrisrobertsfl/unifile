package com.ingenifi.unifile.model.generators.jira

import com.ingenifi.unifile.model.document.*
import com.ingenifi.unifile.model.generators.KeywordExtractor
import com.ingenifi.unifile.VerbosePrinter
import com.ingenifi.unifile.VerbosePrinting
import com.ingenifi.unifile.Verbosity

data class BugCreator(val bug: Bug, val keywordExtractor: KeywordExtractor, val verbosity: Verbosity) : SectionCreator, VerbosePrinting by VerbosePrinter(verbosity) {
    override fun create(sectionNumber: SectionNumber): List<Section> {
        verbosePrint("Processing Story '${bug.key} - ${bug.title}'")
        val headingName = Name("Jira Bug")
        val title = TitleText.Title(bug.title)
        val heading = Heading(headingName = headingName, sectionNumber = sectionNumber, title = title)
        val keywords = KeywordsText.Keywords(keywordExtractor.extract(bug.detail))
        val text = UnifileBodyText(headingName, keywords = keywords, detail = DetailText.Detail(bug.detail))
        val section = Section(heading = heading, bodyText = text)
        return listOf(section)
    }
}