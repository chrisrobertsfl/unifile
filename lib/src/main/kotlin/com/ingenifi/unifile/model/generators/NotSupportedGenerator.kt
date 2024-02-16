package com.ingenifi.unifile.model.generators

import com.ingenifi.unifile.VerbosePrinter
import com.ingenifi.unifile.VerbosePrinting
import com.ingenifi.unifile.model.document.Section
import java.io.File

data class NotSupportedGenerator(val config: SectionGeneratorConfig, val file: File) : SectionGenerator, VerbosePrinting by VerbosePrinter(config.verbosity) {

    override fun generate(): List<Section> {
        verbosePrint("Warning cannot process file with extension ${file.extension}")
        return emptyList()
    }

}