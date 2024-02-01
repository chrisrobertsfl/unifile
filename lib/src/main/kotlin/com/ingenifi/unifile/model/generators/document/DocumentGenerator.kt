package com.ingenifi.unifile.model.generators.document

import com.ingenifi.unifile.model.document.*
import com.ingenifi.unifile.model.document.TitleText.Title

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
        val sortedSections = document.body.sections.sortedWith(Comparator { s1, s2 ->
            compareSectionNumbers(s1.heading.sectionNumber, s2.heading.sectionNumber)
        })
        sortedSections.forEach { appendSection(it) }
    }

    private fun StringBuilder.appendSection(section: Section) {
        appendLine(borderLine)
        appendLine(formatHeadingWithSectionNumber(section.heading))
        appendLine(borderLine)
        appendText(section.bodyText)
    }

    private fun StringBuilder.appendText(text: BodyText) {
        appendLine(text.content)
        appendLine()
    }

    private fun StringBuilder.appendHeading(heading: Heading) = appendLine(formatHeadingWithSectionNumber(heading))

    private fun createEntryLine(heading: Heading): String {
        val separator = if (heading.isNameAndTitlePresent()) " - " else ""
        val headingName = heading.headingName.takeIf { it is Name }?.content ?: ""
        val title = heading.title.takeIf { it is Title }?.content ?: ""
        return listOf(headingName, title).joinToString(separator = separator)
    }
    private fun formatHeadingWithSectionNumber(heading: Heading): String = "${generateSectionNumber(heading.sectionNumber).padStartIfNeeded(maxSectionLength, ' ', justifySectionNumbers)} ${createEntryLine(heading)}"
    private fun generateSectionNumber(sectionNumber: SectionNumber) = sectionNumber.levels.joinToString(separator = ".") { "${it.number}" } + "."
    private fun determineMaximumSectionLength() = document.tableOfContents.headings.maxOfOrNull { generateSectionNumber(it.sectionNumber).length } ?: 0
    private fun determineMaximumHeadingLength(): Int {
        val tocHeadings = document.tableOfContents.headings
        val bodySectionHeadings = document.body.sections.map { it.heading }
        val allHeadings = tocHeadings + bodySectionHeadings
        return allHeadings.maxOfOrNull { heading -> formatHeadingWithSectionNumber(heading).length } ?: 0
    }
    private fun String.padStartIfNeeded(length: Int, padChar: Char, justify: Boolean) = if (justify) this.padStart(length, padChar) else this
}
