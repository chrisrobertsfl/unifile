package com.ingenifi.unifile

import com.ingenifi.unifile.formatter.KeywordExtractor
import com.ingenifi.unifile.input.InputPaths
import com.ingenifi.unifile.model.document.Document
import com.ingenifi.unifile.model.document.DocumentGenerator
import com.ingenifi.unifile.model.document.SectionGenerator
import com.ingenifi.unifile.model.document.SectionGeneratorFactory
import com.ingenifi.unifile.output.OutputPath
import io.ktor.client.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking

// TODO:  Hard coded dispatcher
data class UniFileRunner(
    val input: InputPaths,
    val client: HttpClient = UnsecuredHttpClient.create(),
    val keywordExtractor: KeywordExtractor = KeywordExtractor(),
    val parameterStore: ParameterStore = ParameterStore.NONE,
    val verbosity: Verbosity
) : VerbosePrinting by VerbosePrinter(verbosity) {

    private val sectionGeneratorFactory = SectionGeneratorFactory(config = SectionGenerator.Config(keywordExtractor, verbosity))
    fun combineFiles(output: OutputPath) = runBlocking {
        verbosePrint("Processing files")
        val withLevel = verbosity.increasedLevel()
        val sections = input.allFiles().mapIndexed { index, file ->
            async(Dispatchers.IO) {
                verbosePrint("Processing file #${index + 1}: $file", withLevel = withLevel)
                val sectionGenerator = sectionGeneratorFactory.create(file, index + 1)
                sectionGenerator.generate()
            }
        }.awaitAll().flatten()
        val text = DocumentGenerator(document = Document(sections), justifySectionNumbers = true).generate()
        output.write(text = text)
    }
}