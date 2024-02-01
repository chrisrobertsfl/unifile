package com.ingenifi.unifile

import com.ingenifi.unifile.model.document.Document
import com.ingenifi.unifile.model.generators.KeywordExtractor
import com.ingenifi.unifile.model.generators.SectionGeneratorConfig
import com.ingenifi.unifile.model.generators.SectionGeneratorFactory
import com.ingenifi.unifile.model.generators.document.DocumentGenerator
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

    private val sectionGeneratorFactory = SectionGeneratorFactory(config = SectionGeneratorConfig(keywordExtractor = keywordExtractor, verbosity = verbosity.increasingBy(by = 2), parameterStore = parameterStore))
    fun combineFiles(output: OutputPath) = runBlocking {
        val allFiles = input.allFiles()
        verbosePrint("Processing ${allFiles.size} file(s)")
        val withLevel = verbosity.increasedLevel()
        val sections = allFiles.mapIndexed { index, file ->
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