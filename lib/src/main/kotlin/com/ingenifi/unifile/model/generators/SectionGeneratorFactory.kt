package com.ingenifi.unifile.model.generators

import com.ingenifi.unifile.model.generators.html.HtmlGenerator
import com.ingenifi.unifile.model.generators.pdf.PdfGenerator
import com.ingenifi.unifile.model.generators.powerpoint.PowerPointGenerator
import com.ingenifi.unifile.model.generators.text.TextGenerator
import com.ingenifi.unifile.model.generators.transcript.TranscriptGenerator
import com.ingenifi.unifile.model.generators.word.WordGenerator
import com.ingenifi.unifile.model.generators.xml.XmlGenerator
import java.io.File

data class SectionGeneratorFactory(val config: SectionGeneratorConfig) {
    fun create(file: File, number: Int): SectionGenerator = when (file.extension.lowercase()) {
        "csv" -> TextGenerator(config = config, number = number, file = file, headingNameString = "Csv Document")
        "docx" -> WordGenerator(config = config, number = number, file = file, headingNameString = "Csv Document")
        "html" -> HtmlGenerator(config = config, number = number, file = file)
        "json" -> TextGenerator(config = config, number = number, file = file, headingNameString = "Json Document")
        "pdf" -> PdfGenerator(config = config, number = number, file = file)
        "pptx" -> PowerPointGenerator(config = config, number = number, file = file)
        "transcript" -> TranscriptGenerator(config = config, number = number, file = file)
        "txt" -> TextGenerator(config = config, number = number, file = file)
        "xml" -> XmlGenerator(config = config, number = number, file = file)
        else -> throw IllegalArgumentException("Unknown formatter for extension ${file.extension.lowercase()}: given ${file.name}")
    }
}