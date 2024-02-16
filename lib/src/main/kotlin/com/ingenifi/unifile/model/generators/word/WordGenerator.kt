package com.ingenifi.unifile.model.generators.word

import com.ingenifi.unifile.model.document.DetailText
import com.ingenifi.unifile.model.document.HeadingName
import com.ingenifi.unifile.model.document.Name
import com.ingenifi.unifile.model.generators.FileGenerator
import com.ingenifi.unifile.model.generators.SectionsGenerator
import com.ingenifi.unifile.model.generators.SectionGeneratorConfig
import java.io.File

data class WordGenerator(
    val config: SectionGeneratorConfig, val number: Int, val file: File, val headingName: HeadingName = HEADING_NAME, val detail: DetailText = DetailText.Detail(
        WordConverter().convert(file))
) : SectionsGenerator by FileGenerator(config, number, file, headingName = headingName, detail = detail) {
    companion object {
        val HEADING_NAME = Name("Word Document")
    }
}