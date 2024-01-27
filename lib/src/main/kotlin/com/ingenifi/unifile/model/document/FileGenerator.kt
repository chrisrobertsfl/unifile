package com.ingenifi.unifile.model.document

import com.ingenifi.unifile.VerbosePrinter
import com.ingenifi.unifile.VerbosePrinting
import java.io.File

data class FileGenerator(val config: SectionGenerator.Config, val number: Int, val file: File, val headingName: HeadingName, val detail: String = file.readText()) : SectionGenerator,
    VerbosePrinting by VerbosePrinter(config.verbosity) {
    override fun sections(): List<Section> {
        verbosePrint("detail is $detail")
        val title = file.name
        val keywords = config.keywordExtractor.extract(title) + config.keywordExtractor.extractKeywords(file)
        return listOf(
            Section(
                heading = Heading(
                    headingName = headingName, sectionNumber = SectionNumber(listOf(Level(number))), title = Title(content = title)
                ), text = UnifileBodyText(headingName = headingName, keywords = KeywordsText.Keywords(keywords), detail = DetailText.Detail(detail))
            )
        )
    }
}