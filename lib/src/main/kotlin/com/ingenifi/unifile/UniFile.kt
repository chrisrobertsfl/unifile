package com.ingenifi.unifile

import com.ingenifi.unifile.content.Content
import com.ingenifi.unifile.content.KeywordExtractor
import com.ingenifi.unifile.content.formatter.DocumentFormatter
import com.ingenifi.unifile.input.InputPaths
import com.ingenifi.unifile.output.OutputPath
import org.slf4j.LoggerFactory


// TODO:  Fix verbosity throughout the process
// TODO:  Refactor in to smaller chunks
// TODO:  Allowable extensions to funnel into plain text
// TODO:  Simplify content converters with reading from pdf and asStream
// TODO:  More formal testing

data class UniFile(val input: InputPaths, val maxFileSizeMB: Int = 19, val ejectBlankLines: Boolean = true, val verbose: Boolean = false) {
    private val logger by lazy { LoggerFactory.getLogger(UniFile::class.java) }

    fun combineFiles(output: OutputPath) {
        val keywordExtractor = KeywordExtractor()
        val client = UnsecuredHttpClient.create()
        var documentNumber = 1
        val documents = input.list.flatMap { it.findFiles() }.map {
            if (verbose) println("$documentNumber Processing file:  $it")
            DocumentFormatter.from(it, keywordExtractor, client).format(documentNumber++)
        }.joinToString("\n")
        output.write(documents)
    }

    companion object {
        val STOP_WORDS_RESOURCE = "/all-stop-words.txt"
    }

}

data class ContentDocumentFormatter(private val startAt: Int = 1) {
    fun format(contents: MutableList<Content>): String {
        return ""
    }
}


