package com.ingenifi.unifile.model.generators.text

import com.ingenifi.unifile.model.generators.FileGenerator
import com.ingenifi.unifile.model.document.Name
import com.ingenifi.unifile.model.generators.SectionGenerator
import com.ingenifi.unifile.model.generators.SectionGeneratorConfig
import com.ingenifi.unifile.verbosity.VerbosePrinter
import com.ingenifi.unifile.verbosity.VerbosePrinting
import java.io.File

data class TextGenerator(val config: SectionGeneratorConfig, val number: Int, val file: File, val headingNameString: String = "Text Document") :
    SectionGenerator by FileGenerator(config, number, file, Name(headingNameString)), VerbosePrinting by VerbosePrinter(config.verbosity)