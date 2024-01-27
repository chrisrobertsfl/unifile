package com.ingenifi.unifile.model.document

import com.ingenifi.unifile.VerbosePrinter
import com.ingenifi.unifile.VerbosePrinting
import com.ingenifi.unifile.Verbosity
import com.ingenifi.unifile.formatter.KeywordExtractor
import com.ingenifi.unifile.formatter.pdf.PdfConverter
import com.ingenifi.unifile.model.document.DetailText.Detail
import com.ingenifi.unifile.model.document.KeywordsText.Keywords
import com.ingenifi.unifile.model.document.SectionGenerator.Config
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.text.PDFTextStripper
import java.io.File

class SectionGeneratorSpec : FeatureSpec({
    feature("Using files") {
        val keywordExtractor = mockk<KeywordExtractor>()
        val expectedExtractedKeywords = listOf("k1", "k2")
        every { keywordExtractor.extract(any(), any()) } returns expectedExtractedKeywords
        val expectedExtractedFileKeywords = listOf("simple", "txt")
        every { keywordExtractor.extractKeywords(any()) } returns expectedExtractedFileKeywords
        val allKeywords = expectedExtractedKeywords + expectedExtractedFileKeywords
        val verbosity = Verbosity(verbose = true, level = 0)
        val config = Config(keywordExtractor = keywordExtractor, verbosity = verbosity)
        val file = File("src/test/resources/simple.txt")
        val headingNameString = "Text Document"
        val number = 1

        scenario("txt using raw file generator") {
            val generator = FileGenerator(file = file, headingName = Name(headingNameString), number = number, config = config)
            val sections = generator.sections()
            sections shouldBe expectedSection(allKeywords, "Text Document", "simple.txt", "Hello Plain Text")

            val tableOfContents = TableOfContents(headings = sections.map { it.heading })
            val body = Body(sections = sections)
            val document = Document(tableOfContents = tableOfContents, body = body)
            println(DocumentGenerator(document = document, justifySectionNumbers = true).generate())
        }

        scenario("plain text using text generator") {
            TextGenerator(config, number, file).sections() shouldBe expectedSection(allKeywords, "Text Document", "simple.txt", "Hello Plain Text")
        }

        scenario("json using text generator") {
            val generator = TextGenerator(config, number, file = File("src/test/resources/simple.json"), headingNameString = "Json Document")
            val sections = generator.sections()
            sections shouldBe expectedSection(allKeywords, "Json Document", "simple.json", "{ \"message\" : \"Hello Json\" }")
        }

        scenario("pdf using generator by delegation") {
            PdfGenerator(config, number, file = File("src/test/resources/simple.pdf")).sections() shouldBe expectedSection(allKeywords, "Pdf Document", "simple.pdf", "Hello PDF")
        }
    }
})

private fun expectedSection(allKeywords: List<String>, name: String, title: String, detail: String) = listOf(
    Section(
        heading = Heading(
            headingName = Name(name), sectionNumber = SectionNumber(listOf(Level(1))), title = Title(title)
        ), text = UnifileBodyText(headingName = Name(name), keywords = Keywords(allKeywords), detail = Detail(detail))
    )
)

interface SectionGenerator {
    fun sections() = listOf<Section>()

    data class Config(val keywordExtractor: KeywordExtractor, val verbosity: Verbosity)
}

data class FileGenerator(val config: Config, val number: Int, val file: File, val headingName: HeadingName, val detail: String = file.readText()) : SectionGenerator,
    VerbosePrinting by VerbosePrinter(config.verbosity) {
    override fun sections(): List<Section> {
        verbosePrint("detail is $detail")
        val title = file.name
        val keywords = config.keywordExtractor.extract(title) + config.keywordExtractor.extractKeywords(file)
        return listOf(
            Section(
                heading = Heading(
                    headingName = headingName, sectionNumber = SectionNumber(listOf(Level(number))), title = Title(content = title)
                ), text = UnifileBodyText(headingName = headingName, keywords = Keywords(keywords), detail = Detail(detail))
            )
        )
    }
}

data class TextGenerator(val config: Config, val number: Int, val file: File, val headingNameString: String = "Text Document") :
    SectionGenerator by FileGenerator(config, number, file, Name(headingNameString))

data class PdfGenerator(val config: Config, val number: Int, val file: File, val headingNameString: String = "Pdf Document", val detail: String = PdfConverter().convert(file)) :
    SectionGenerator by FileGenerator(config, number, file, Name(headingNameString), detail)




