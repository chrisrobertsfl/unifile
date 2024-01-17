package com.ingenifi.unifile

import com.ingenifi.unifile.content.Content
import com.ingenifi.unifile.content.Contents
import com.ingenifi.unifile.content.KeywordExtractor
import com.ingenifi.unifile.content.conversion.ContentConverter
import com.ingenifi.unifile.input.InputPaths
import com.ingenifi.unifile.output.OutputPath
import org.slf4j.LoggerFactory
import java.nio.charset.StandardCharsets


// TODO:  Fix verbosity throughout the process
// TODO:  Refactor in to smaller chunks
// TODO:  Allowable extensions to funnel into plain text
// TODO:  Simplify content converters with reading from pdf and asStream
// TODO:  More formal testing

data class UniFile(val input: InputPaths, val maxFileSizeMB: Int = 19, val ejectBlankLines: Boolean = true, val verbose: Boolean = false) {
    private val logger by lazy { LoggerFactory.getLogger(UniFile::class.java) }

    fun combineFiles(output: OutputPath) {
        val stopWords = gatherStopWords()
        val keywordExtractor = KeywordExtractor(stopWords = stopWords)
        val contents = Contents()
        input.list.flatMap { it.findFiles() }.map {
            if (verbose) println("o Processing file:  $it")
            ContentConverter.from(it.extension, keywordExtractor, verbose).convert(it)
        }.forEach { contents.add(it) }
        val out = ContentDocumentFormatter(startAt = 1).format(contents.contents)
        output.write(contents.toJsonString())
    }

    private fun gatherStopWords(): List<String> {
        if (verbose) println("o Gathering stop words from $STOP_WORDS_RESOURCE")
        val resourceAsStream = this::class.java.getResourceAsStream(STOP_WORDS_RESOURCE)
        if (resourceAsStream != null) {
            return resourceAsStream.bufferedReader(StandardCharsets.UTF_8).readLines()
        } else {
            throw IllegalArgumentException("Resource not found: $STOP_WORDS_RESOURCE")
        }
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


