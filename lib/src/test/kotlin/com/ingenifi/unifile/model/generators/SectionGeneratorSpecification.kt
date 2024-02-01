package com.ingenifi.unifile.model.generators

import com.ingenifi.unifile.model.document.*
import com.ingenifi.unifile.model.document.DetailText.Detail
import com.ingenifi.unifile.model.document.KeywordsText.Keywords
import com.ingenifi.unifile.model.generators.document.DocumentGenerator
import com.ingenifi.unifile.model.generators.pdf.PdfGenerator
import com.ingenifi.unifile.model.generators.text.TextGenerator
import com.ingenifi.unifile.model.generators.xml.XmlGenerator
import com.ingenifi.unifile.Verbosity
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import java.io.File

class SectionGeneratorSpecification : FeatureSpec({

    fun expectedSection(allKeywords: List<String>, name: String, title: String, detail: String) = listOf(
        Section(
            heading = Heading(
                headingName = Name(name), sectionNumber = SectionNumber(listOf(Level(1))), title = TitleText.Title(title)
            ), bodyText = UnifileBodyText(headingName = Name(name), keywords = Keywords(allKeywords), detail = Detail(detail))
        )
    )

    feature("Using files") {
        val keywordExtractor = mockk<KeywordExtractor>()
        val expectedExtractedKeywords = listOf("k1", "k2")
        every { keywordExtractor.extract(any(), any()) } returns expectedExtractedKeywords
        val expectedExtractedFileKeywords = listOf("simple", "txt")
        every { keywordExtractor.extractKeywords(any()) } returns expectedExtractedFileKeywords
        val allKeywords = expectedExtractedKeywords + expectedExtractedFileKeywords
        val verbosity = Verbosity(verbose = true, level = 0)
        val config = SectionGeneratorConfig(keywordExtractor = keywordExtractor, verbosity = verbosity)
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
            val generator = TextGenerator(config, number, file = File("src/test/resources/simple.json"), headingName = Name("Json Document"))
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





