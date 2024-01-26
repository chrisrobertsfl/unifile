package com.ingenifi.unifile.model.document

import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk

class DocumentGeneratorSpec : FeatureSpec({
    feature("table of contents") {
        val body = mockk<Body>()
        every { body.sections } returns listOf()
        val document = Document(
            tableOfContents = TableOfContents(
                headings = listOf(
                    Heading(sectionNumber = SectionNumber(levels = listOf(Level(1))), title = Title("My first title")),
                    Heading(sectionNumber = SectionNumber(levels = listOf(Level(1), Level(1))), title = Title("My first sub title")),
                    Heading(sectionNumber = SectionNumber(levels = listOf(Level(2))), title = Title("My second title")),
                    Heading(sectionNumber = SectionNumber(levels = listOf(Level(2), Level(1))), title = Title("My second sub title")),
                    Heading(sectionNumber = SectionNumber(levels = listOf(Level(2), Level(1), Level(1))), title = Title("My second sub sub title")),
                )
            ), body = body
        )

        scenario("generate with no justification") {
            DocumentGenerator(document).generate() shouldBe """
            Table Of Contents
            1. My first title
            1.1. My first sub title
            2. My second title
            2.1. My second sub title
            2.1.1. My second sub sub title
            
        """.trimIndent()
        }

        scenario("generate with justification") {
            DocumentGenerator(document = document, justifySectionNumbers = true).generate() shouldBe """
            Table Of Contents
                1. My first title
              1.1. My first sub title
                2. My second title
              2.1. My second sub title
            2.1.1. My second sub sub title
            
        """.trimIndent()
        }
    }
    feature("body") {
        scenario("simple") {
            val tableOfContents = mockk<TableOfContents>()
            every { tableOfContents.header } returns ""
            every { tableOfContents.headings } returns listOf()
            val document = Document(
                tableOfContents = tableOfContents, body = Body(
                    sections = listOf(
                        Section(heading = Heading(sectionNumber = SectionNumber(levels = listOf(Level(1))), title = Title("My first title")), text = Text("My first text"))
                    )
                )
            )
            DocumentGenerator(document).generate() shouldBe """
                1. My first title
                My first text
            """.trimIndent()
        }
    }
})

data class DocumentGenerator(val document: Document, val justifySectionNumbers: Boolean = false) {
    private val maxSectionLength = if (justifySectionNumbers) determineMaximumSectionLength() else 0
    fun generate() = buildString {
        appendTableOfContents()
        appendBody()
    }

    private fun StringBuilder.appendTableOfContents() = if (document.tableOfContents.headings.isNotEmpty()) {
        appendLine(document.tableOfContents.header)
        appendHeadings()
    } else {
    }

    private fun StringBuilder.appendHeadings() = document.tableOfContents.headings.forEach { appendHeading(it) }
    private fun StringBuilder.appendBody() = document.body.sections.forEach { appendSection(it) }
    private fun StringBuilder.appendSection(section: Section) {
        appendLine(formatHeadingWithSectionNumber(section.heading))
        appendText(section.text)
    }

    private fun StringBuilder.appendText(text: Text) = append(text.content)
    private fun StringBuilder.appendHeading(heading: Heading) = appendLine(formatHeadingWithSectionNumber(heading))
    private fun determineMaximumSectionLength() = document.tableOfContents.headings.maxOfOrNull { generateSectionNumber(it.sectionNumber).length } ?: 0
    private fun formatHeadingWithSectionNumber(heading: Heading): String {
        val sectionNumber = generateSectionNumber(heading.sectionNumber)
        val justifiedSectionNumber = sectionNumber.padStartIfNeeded(maxSectionLength, ' ', justifySectionNumbers)
        return "$justifiedSectionNumber ${heading.title.content}"
    }

    private fun generateSectionNumber(sectionNumber: SectionNumber): String = sectionNumber.levels.joinToString(separator = ".") { "${it.number}" } + "."
    private fun String.padStartIfNeeded(length: Int, padChar: Char, justify: Boolean) = if (justify) this.padStart(length, padChar) else this
}