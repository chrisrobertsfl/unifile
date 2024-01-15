package com.ingenifi.unifile

import com.ingenifi.unifile.content.Contents
import com.ingenifi.unifile.content.KeywordExtractor
import com.ingenifi.unifile.content.conversion.ContentConverter
import com.ingenifi.unifile.input.InputPaths
import com.ingenifi.unifile.output.OutputPath
import java.nio.charset.StandardCharsets


// TODO:  Fix verbosity throughout the process
// TODO:  Refactor in to smaller chunks
// TODO:  Allowable extensions to funnel into plain text
// TODO:  Simplify content converters with reading from pdf and asStream
// TODO:  More formal testing

data class UniFile(val input: InputPaths, val maxFileSizeMB: Int = 19, val ejectBlankLines: Boolean = true) {
    fun combineFiles(output: OutputPath) {
        val stopWords: List<String>
        val resourcePath = "/all-stop-words.txt" // Path to your resource file
        val resourceAsStream = this::class.java.getResourceAsStream(resourcePath)
        if (resourceAsStream != null) {
            stopWords = resourceAsStream.bufferedReader(StandardCharsets.UTF_8).readLines()
        } else {
            throw IllegalArgumentException("Resource not found: $resourcePath")
        }
        val keywordExtractor = KeywordExtractor(stopWords = stopWords)
        val contents = Contents()
        input.list.flatMap { it.findFiles() }.map {
            ContentConverter.from(it.extension, keywordExtractor).convert(it)
        }.forEach { contents.add(it) }
        output.write(contents.toJsonString())
    }

}


