package com.ingenifi.unifile.model.generators.pdf

import com.ingenifi.unifile.model.document.DetailText
import com.ingenifi.unifile.model.document.HeadingName
import com.ingenifi.unifile.model.document.Name
import com.ingenifi.unifile.model.generators.FileGenerator
import com.ingenifi.unifile.model.generators.SectionGenerator
import com.ingenifi.unifile.model.generators.SectionGeneratorConfig
import java.io.File

data class PdfGenerator(
    val config: SectionGeneratorConfig, val number: Int, val file: File, val headingName: HeadingName = HEADING_NAME, val detail: DetailText = DetailText.Detail(
        PdfConverter().convert(
            file
        )
    )
) : SectionGenerator by FileGenerator(config, number, file, headingName = headingName, detail = detail) {
    companion object {
        val HEADING_NAME = Name("Pdf Document")
    }
}