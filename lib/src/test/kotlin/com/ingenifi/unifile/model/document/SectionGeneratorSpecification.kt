package com.ingenifi.unifile.model.document

import com.ingenifi.unifile.Verbosity
import com.ingenifi.unifile.formatter.KeywordExtractor
import com.ingenifi.unifile.model.document.DetailText.Detail
import com.ingenifi.unifile.model.document.KeywordsText.Keywords
import com.ingenifi.unifile.model.document.SectionGenerator.Config
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import java.io.File

class SectionGeneratorSpecification : FeatureSpec({
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
            val sections = generator.generate()
            sections shouldBe expectedSection(allKeywords, "Text Document", "simple.txt", "Hello Plain Text")

            val tableOfContents = TableOfContents(headings = sections.map { it.heading })
            val body = Body(sections = sections)
            val document = Document(tableOfContents = tableOfContents, body = body)
            println(DocumentGenerator(document = document, justifySectionNumbers = true).generate())
        }

        scenario("plain text using text generator") {
            TextGenerator(config, number, file).generate() shouldBe expectedSection(allKeywords, "Text Document", "simple.txt", "Hello Plain Text")
        }

        scenario("json using text generator") {
            val generator = TextGenerator(config, number, file = File("src/test/resources/simple.json"), headingNameString = "Json Document")
            val sections = generator.generate()
            sections shouldBe expectedSection(allKeywords, "Json Document", "simple.json", "{ \"message\" : \"Hello Json\" }")
        }

        scenario("pdf using generator by delegation") {
            PdfGenerator(config, number, file = File("src/test/resources/simple.pdf")).generate() shouldBe expectedSection(allKeywords, "Pdf Document", "simple.pdf", "Hello PDF")
        }

        scenario("xml using generator by delegation") {
            XmlGenerator(config, number, file = File("src/test/resources/simple.xml")).generate() shouldBe expectedSection(
                allKeywords, "Xml Document", "simple.xml", """
           tag []:
             Hello XML
       """.trimIndent()
            )
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




