package com.ingenifi.unifile.model.document

data class DocumentGenerator(val document: Document, val justifySectionNumbers: Boolean = false) {
    private val maxSectionLength = if (justifySectionNumbers) determineMaximumSectionLength() else 0
    private val maxHeadingLength = determineMaximumHeadingLength()
    private val borderLine = "=".repeat(maxHeadingLength) // Computed once and reused

    fun generate() = buildString {
        appendTableOfContents()
        appendBody()
    }

    private fun StringBuilder.appendTableOfContents() {
        if (document.tableOfContents.headings.isNotEmpty()) {
            appendLine(borderLine)
            appendLine(document.tableOfContents.header)
            appendLine(borderLine)
            appendHeadings()
        }
    }

    private fun StringBuilder.appendHeadings() {
        document.tableOfContents.headings.forEach { appendHeading(it) }
    }

    private fun StringBuilder.appendBody() {
        document.body.sections.forEach { appendSection(it) }
    }

    private fun StringBuilder.appendSection(section: Section) {
        appendLine(borderLine)
        appendLine(formatHeadingWithSectionNumber(section.heading))
        appendLine(borderLine)
        appendText(section.text)
    }

    private fun StringBuilder.appendText(text: Text) {
        appendLine(text.content)
        appendLine() // Adding a line break after each text for separation
    }

    private fun StringBuilder.appendHeading(heading: Heading) {
        appendLine(formatHeadingWithSectionNumber(heading))
    }

    private fun formatHeadingWithSectionNumber(heading: Heading): String {
        val sectionNumber = generateSectionNumber(heading.sectionNumber)
        val justifiedSectionNumber = sectionNumber.padStartIfNeeded(maxSectionLength, ' ', justifySectionNumbers)
        return "$justifiedSectionNumber ${heading.title.content}"
    }

    private fun generateSectionNumber(sectionNumber: SectionNumber): String {
        return sectionNumber.levels.joinToString(separator = ".") { "${it.number}" } + "."
    }

    private fun determineMaximumSectionLength() = document.tableOfContents.headings.maxOfOrNull { generateSectionNumber(it.sectionNumber).length } ?: 0

    private fun determineMaximumHeadingLength(): Int {
        return (document.tableOfContents.headings + document.body.sections.map { it.heading })
            .map { heading -> formatHeadingWithSectionNumber(heading).length }
            .maxOrNull() ?: 0
    }

    private fun String.padStartIfNeeded(length: Int, padChar: Char, justify: Boolean) = if (justify) this.padStart(length, padChar) else this
}
