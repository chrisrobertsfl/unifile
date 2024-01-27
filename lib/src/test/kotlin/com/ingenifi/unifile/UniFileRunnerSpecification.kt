package com.ingenifi.unifile

import com.ingenifi.unifile.formatter.KeywordExtractor
import com.ingenifi.unifile.input.InputPaths
import com.ingenifi.unifile.model.document.*
import com.ingenifi.unifile.model.document.SectionGenerator.Config
import com.ingenifi.unifile.output.OutputPath
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.ktor.client.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import java.io.File

class UniFileRunnerSpecification : StringSpec({
    fun resourcesWithExtensions(vararg extensions: String) = extensions.map { "src/test/resources/simple.$it" }
    fun expectedDocument() = (UniFileRunnerSpecification::class.java.classLoader.getResource("expected-document.txt")?.readText()
        ?: throw IllegalArgumentException("Template resource could not be read: expected-document.txt"))

    "run" {
        val input = InputPaths(resourcesWithExtensions("txt", "xml", "pdf", "json", "csv", "docx"))
        val output = OutputPath.StringOutputPath()
        UniFileRunner(input = input, verbosity = Verbosity(verbose = true, level = 0)).combineFiles(output)
        output.contents shouldBe expectedDocument()
    }
})


data class UniFileRunner(
    val input: InputPaths,
    val client: HttpClient = UnsecuredHttpClient.create(),
    val keywordExtractor: KeywordExtractor = KeywordExtractor(),
    val parameterStore: ParameterStore = ParameterStore.NONE,
    val verbosity: Verbosity
) : VerbosePrinting by VerbosePrinter(verbosity) {

    private val sectionGeneratorFactory = SectionGeneratorFactory(config = Config(keywordExtractor, verbosity))
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

data class SectionGeneratorFactory(val config: Config) {
    fun create(file: File, number: Int): SectionGenerator = when (file.extension.lowercase()) {
        "csv" -> TextGenerator(config = config, number = number, file = file, headingNameString = "Csv Document")
        "docx" -> WordGenerator(config = config, number = number, file = file, headingNameString = "Csv Document")
        "json" -> TextGenerator(config = config, number = number, file = file, headingNameString = "Json Document")
        "pdf" -> PdfGenerator(config = config, number = number, file = file)
        "pptx" -> PowerPointGenerator(config = config, number = number, file = file)
        "txt" -> TextGenerator(config = config, number = number, file = file)
        "xml" -> XmlGenerator(config = config, number = number, file = file)
        else -> throw IllegalArgumentException("Unknown formatter for extension ${file.extension.lowercase()}: given ${file.name}")
    }
}