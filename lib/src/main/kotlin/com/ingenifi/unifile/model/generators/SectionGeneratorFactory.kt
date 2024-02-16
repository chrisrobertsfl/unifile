package com.ingenifi.unifile.model.generators

import com.ingenifi.unifile.model.document.Name
import com.ingenifi.unifile.model.generators.confluence.ConfluenceGenerator
import com.ingenifi.unifile.model.generators.excel.ExcelGenerator
import com.ingenifi.unifile.model.generators.html.HtmlGenerator
import com.ingenifi.unifile.model.generators.jira.JiraGenerator
import com.ingenifi.unifile.model.generators.kotlin.KotlinGenerator
import com.ingenifi.unifile.model.generators.pdf.PdfGenerator
import com.ingenifi.unifile.model.generators.powerpoint.PowerPointGenerator
import com.ingenifi.unifile.model.generators.text.TextGenerator
import com.ingenifi.unifile.model.generators.transcript.TranscriptGenerator
import com.ingenifi.unifile.model.generators.word.WordGenerator
import com.ingenifi.unifile.model.generators.xml.XmlGenerator
import java.io.File

data class SectionGeneratorFactory(val config: SectionGeneratorConfig) {
    fun create(file: File, number: Int): SectionGenerator = when (file.extension.lowercase()) {
        "confluence" -> ConfluenceGenerator(config = config, number = number, file = file)
        "csv" -> TextGenerator(config = config, number = number, file = file, headingName = Name("Csv Document"))
        "docx" -> WordGenerator(config = config, number = number, file = file)
        "html" -> HtmlGenerator(config = config, number = number, file = file)
        "json" -> TextGenerator(config = config, number = number, file = file, headingName = Name("Json Document"))
        "jira" -> JiraGenerator(config = config, number = number, file = file)
        "kt" -> KotlinGenerator(config = config, number = number, file = file)
        "pdf" -> PdfGenerator(config = config, number = number, file = file)
        "pptx" -> PowerPointGenerator(config = config, number = number, file = file)
        "transcript" -> TranscriptGenerator(config = config, number = number, file = file)
        "txt" -> TextGenerator(config = config, number = number, file = file)
        "xlsx" -> ExcelGenerator(config = config, number = number, file = file)
        "xml" -> XmlGenerator(config = config, number = number, file = file)
        //else -> throw IllegalArgumentException("Unknown formatter for extension ${file.extension.lowercase()}: given ${file.name}")
        else -> NotSupportedGenerator(config = config, file = file)
    }
}