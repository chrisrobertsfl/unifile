package com.ingenifi.unifile.model.generators.transcript

import com.ingenifi.unifile.model.document.DetailText
import com.ingenifi.unifile.model.document.HeadingName
import com.ingenifi.unifile.model.document.Name
import com.ingenifi.unifile.model.document.SummaryText
import com.ingenifi.unifile.model.generators.FileGenerator
import com.ingenifi.unifile.model.generators.SectionGenerator
import com.ingenifi.unifile.model.generators.SectionGeneratorConfig
import java.io.File

data class TranscriptGenerator(
    val config: SectionGeneratorConfig,
    val number: Int,
    val file: File,
    val headingName: HeadingName = HEADING_NAME,
    private val transcript: Transcript = TranscriptConverter().convert(file)
) : SectionGenerator by FileGenerator(config, number, file, headingName, summary = SummaryText.Summary(transcript.summary), detail = DetailText.Detail(transcript.detail)) {
    companion object {
        val HEADING_NAME = Name("Transcript Document")
    }
}