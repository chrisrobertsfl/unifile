package com.ingenifi.unifile.model.document

data class DocumentGenerator(val document: Document, val justifySectionNumbers: Boolean = false) {
    private val maxSectionLength = if (justifySectionNumbers) determineMaximumSectionLength() else 0
    private val maxHeadingLength = determineMaximumHeadingLength()

    fun generate() = buildString {
        appendTableOfContents()
        appendBody()
    }

    private fun StringBuilder.appendTableOfContents() {
        if (document.tableOfContents.headings.isNotEmpty()) {
            val borderLine = "=".repeat(maxHeadingLength)
            appendLine(borderLine)
            appendLine(document.tableOfContents.header)
            appendLine(borderLine)
            appendHeadings()
        }
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
    private fun determineMaximumHeadingLength(): Int = document.tableOfContents.headings
            .map { heading -> formatHeadingWithSectionNumber(heading).length }
            .maxOrNull() ?: 0
    private fun formatHeadingWithSectionNumber(heading: Heading): String {
        val sectionNumber = generateSectionNumber(heading.sectionNumber)
        val justifiedSectionNumber = sectionNumber.padStartIfNeeded(maxSectionLength, ' ', justifySectionNumbers)
        return "$justifiedSectionNumber ${heading.title.content}"
    }

    private fun generateSectionNumber(sectionNumber: SectionNumber): String = sectionNumber.levels.joinToString(separator = ".") { "${it.number}" } + "."
    private fun String.padStartIfNeeded(length: Int, padChar: Char, justify: Boolean) = if (justify) this.padStart(length, padChar) else this
}