package com.ingenifi.unifile.model.generators.document

import com.ingenifi.unifile.model.document.*

data class DocumentGenerator(val document: Document, val justifySectionNumbers: Boolean = false) {
    private val maxSectionLength = if (justifySectionNumbers) determineMaximumSectionLength() else 0
    private val maxHeadingLength = determineMaximumHeadingLength()
    private val borderLine = "=".repeat(maxHeadingLength)

    fun generate() = buildString {
        appendTableOfContents()
        appendBody()
    }.trim().replace("\r\n", "\n")

    private fun StringBuilder.appendTableOfContents() {
        if (document.tableOfContents.headings.isNotEmpty()) {
            val sortedHeadings = document.tableOfContents.headings.sortedWith(Comparator { h1, h2 ->
                compareSectionNumbers(h1.sectionNumber, h2.sectionNumber)
            })
            appendLine(borderLine)
            appendLine(document.tableOfContents.header)
            appendLine(borderLine)
            sortedHeadings.forEach { appendHeading(it) }
            appendLine()
            appendLine()
        }
    }

    private fun compareSectionNumbers(sn1: SectionNumber, sn2: SectionNumber): Int {
        val list1 = sn1.levels.map { it.number }
        val list2 = sn2.levels.map { it.number }
        val commonLength = minOf(list1.size, list2.size)

        for (i in 0 until commonLength) {
            if (list1[i] != list2[i]) {
                return list1[i].compareTo(list2[i])
            }
        }

        return list1.size.compareTo(list2.size)
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

    private fun StringBuilder.appendText(text: BodyText) {
        appendLine(text.content)
        appendLine()
    }

    private fun StringBuilder.appendHeading(heading: Heading) {
        appendLine(formatHeadingWithSectionNumber(heading))
    }

    private fun formatHeadingWithSectionNumber(heading: Heading): String {
        val headingName = when (heading.headingName) {
            is HeadingName.None -> ""
            is Name -> "${heading.headingName.content} - "
        }
        return "${generateSectionNumber(heading.sectionNumber).padStartIfNeeded(maxSectionLength, ' ', justifySectionNumbers)} ${headingName}${heading.title.content}"
    }

    private fun generateSectionNumber(sectionNumber: SectionNumber) = sectionNumber.levels.joinToString(separator = ".") { "${it.number}" } + "."

    private fun determineMaximumSectionLength() = document.tableOfContents.headings.maxOfOrNull { generateSectionNumber(it.sectionNumber).length } ?: 0

    private fun determineMaximumHeadingLength() =
        (document.tableOfContents.headings + document.body.sections.map { it.heading }).map { heading -> formatHeadingWithSectionNumber(heading).length }.maxOrNull() ?: 0

    private fun String.padStartIfNeeded(length: Int, padChar: Char, justify: Boolean) = if (justify) this.padStart(length, padChar) else this
}
