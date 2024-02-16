package com.ingenifi.unifile.model.generators

import com.ingenifi.unifile.VerbosePrinter
import com.ingenifi.unifile.VerbosePrinting
import com.ingenifi.unifile.model.document.Section
import com.ingenifi.unifile.model.document.Sections
import java.io.File

data class NotSupportedGenerator(val config: SectionGeneratorConfig, val file: File) : SectionsGenerator, VerbosePrinting by VerbosePrinter(config.verbosity) {

    override fun generate(): Sections {
        verbosePrint("Warning cannot process file with extension ${file.extension}")
        return Sections(list = emptyList<Section>(), numberProcessed = 0)
    }

}