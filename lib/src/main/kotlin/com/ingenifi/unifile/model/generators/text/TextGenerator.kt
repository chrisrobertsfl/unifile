package com.ingenifi.unifile.model.generators.text

import com.ingenifi.unifile.model.document.HeadingName
import com.ingenifi.unifile.model.document.Name
import com.ingenifi.unifile.model.generators.FileGenerator
import com.ingenifi.unifile.model.generators.SectionGenerator
import com.ingenifi.unifile.model.generators.SectionGeneratorConfig
import com.ingenifi.unifile.VerbosePrinter
import com.ingenifi.unifile.VerbosePrinting
import java.io.File

data class TextGenerator(val config: SectionGeneratorConfig, val number: Int, val file: File, val headingName: HeadingName = HEADING_NAME) :
    SectionGenerator by FileGenerator(config = config, number = number, file = file, headingName = headingName), VerbosePrinting by VerbosePrinter(config.verbosity) {
    companion object {
        val HEADING_NAME = Name("Text Document")
    }
}