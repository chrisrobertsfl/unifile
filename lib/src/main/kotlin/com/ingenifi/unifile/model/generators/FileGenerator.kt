package com.ingenifi.unifile.model.generators

import com.ingenifi.unifile.model.document.*
import com.ingenifi.unifile.model.document.DetailText.Detail
import com.ingenifi.unifile.model.document.KeywordsText.Keywords
import com.ingenifi.unifile.model.document.TitleText.Title
import com.ingenifi.unifile.VerbosePrinter
import com.ingenifi.unifile.VerbosePrinting
import java.io.File

data class FileGenerator(
    val config: SectionGeneratorConfig,
    val number: Int,
    val file: File,
    val headingName: HeadingName,
    val summary: SummaryText = SummaryText.None,
    val detail: DetailText = Detail(file.readText()),
    val title: TitleText = Title(file.name),
    val additionalKeywords: List<String> = listOf()
) : SectionGenerator, VerbosePrinting by VerbosePrinter(config.verbosity) {
    override fun generate(): List<Section> {
        val keywords = config.keywordExtractor.extract(title.content) + config.keywordExtractor.extractKeywords(file) + config.additionalKeywords
        val text = UnifileBodyText(headingName = headingName, keywords = Keywords(keywords), summary = summary, detail = detail)
        return listOf(Section(heading = Heading(headingName = headingName, sectionNumber = SectionNumber(number), title = title), bodyText = text))
    }
}