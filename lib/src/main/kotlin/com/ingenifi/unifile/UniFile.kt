package com.ingenifi.unifile

import com.ingenifi.unifile.formatter.DocumentFormatterFactory
import com.ingenifi.unifile.formatter.KeywordExtractor
import com.ingenifi.unifile.formatter.toc.TableOfContents
import com.ingenifi.unifile.input.InputPaths
import com.ingenifi.unifile.output.OutputPath
import io.ktor.client.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking

// TODO:  More formal testing

data class UniFile(
    val input: InputPaths,
    val client: HttpClient = UnsecuredHttpClient.create(),
    val keywordExtractor: KeywordExtractor = KeywordExtractor(),
    val parameterStore: ParameterStore,
    val toc: TableOfContents,
    val verbosity: Verbosity
) : VerbosePrinting by VerbosePrinter(verbosity) {
    private val documentFormatterFactory =
        DocumentFormatterFactory(parameterStore = parameterStore, keywordExtractor = keywordExtractor, client = client, toc = toc, verbosity = verbosity.increasingBy(by = 2))
    private var documentNumber = 1

    fun combineFiles(output: OutputPath) = runBlocking {
        verbosePrint("Processing files")
        val withLevel = verbosity.increasedLevel()

        // Launching a coroutine for parallel processing
        val documents = input.allFiles().mapIndexed { index, file ->
            async(Dispatchers.IO) {
                verbosePrint("Processing file #${index + 1}: $file", withLevel = withLevel)
                val documentFormatter = documentFormatterFactory.create(file)
                documentFormatter.format(index + 1)
            }
        }.awaitAll().joinToString("\n")

        val entireOutput = buildString {
            if (toc.hasEntries()) {
                append(toc.format())
                append("\n\n\n")
            }
            append(documents)
        }

        output.write(entireOutput)
    }
}




