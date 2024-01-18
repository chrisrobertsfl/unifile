package com.ingenifi.unifile

import com.ingenifi.unifile.formatter.DocumentFormatterFactory
import com.ingenifi.unifile.formatter.KeywordExtractor
import com.ingenifi.unifile.input.InputPaths
import com.ingenifi.unifile.output.OutputPath


// TODO:  Fix verbosity throughout the process
// TODO:  Refactor in to smaller chunks
// TODO:  Allowable extensions to funnel into plain text
// TODO:  Simplify content converters with reading from pdf and asStream
// TODO:  More formal testing

data class UniFile(val input: InputPaths, val verbosity: Verbosity) : VerbosePrinting by VerbosePrinter(verbosity) {
    private val documentFormatterFactory = DocumentFormatterFactory(keywordExtractor = KeywordExtractor(), client = UnsecuredHttpClient.create(), verbosity = verbosity.increasingBy(by = 2))
    private var documentNumber = 1

    fun combineFiles(output: OutputPath) {
        verbosePrint("Processing files")
        val withLevel = verbosity.increasedLevel()
        val documents = input.allFiles().joinToString("\n") {
            verbosePrint("Processing file #${documentNumber}: $it", withLevel = withLevel)
            val documentFormatter = documentFormatterFactory.create(it)
            documentFormatter.format(documentNumber++)
        }
        output.write(documents)
    }
}




