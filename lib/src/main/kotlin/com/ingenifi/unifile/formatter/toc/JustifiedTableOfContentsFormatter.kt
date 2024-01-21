package com.ingenifi.unifile.formatter.toc

import kotlin.math.max

data class JustifiedTableOfContentsFormatter(val leaderChar: Char = '.', val header: Header = Header()) : TableOfContentsFormatter {
    override fun format(entries: List<Entry>): String {
        if (entries.isEmpty()) return ""

        // Collect and sort section numbers
        val sortedSections = SectionSorter(entries.map { it.sectionNumber }).sortSections().distinct()

        // Create a map for quick lookup
        val titleMap = entries.associate { it.sectionNumber to it.title }

        // Format sorted entries
        val maxSectionNumberLength = sortedSections.maxOf { it.toString().length }
        val maxTitleLength = entries.maxOf { it.title.length }
        val totalLength = maxSectionNumberLength + maxTitleLength + 3

        val tocEntries = sortedSections.joinToString(separator = "\n") { sectionNumber ->
            val title = titleMap[sectionNumber] ?: ""
            val sectionNumberStr = sectionNumber.toString()
            val leaderCount = totalLength - sectionNumberStr.length - title.length - 1
            val leaders = leaderChar.toString().repeat(max(0, leaderCount))
            "$sectionNumberStr $leaders $title"
        }

        // Create and format the header
        val maxEntryLength = tocEntries.split("\n").maxOf { it.length }
        val headerString = formatHeader(header, maxEntryLength)

        return headerString + tocEntries
    }

    private fun formatHeader(header: Header, maxWidth: Int): String {
        val headerLines = header.title.wrapText(maxWidth).lines()
        val longestLineLength = headerLines.maxOf { it.length }
        val headerBorder = header.borderChar.toString().repeat(longestLineLength)

        return headerBorder + "\n" + headerLines.joinToString("\n") + "\n" + headerBorder + "\n"
    }

    private fun String.wrapText(maxWidth: Int): String {
        val words = this.split(' ')
        var currentLine = ""
        val lines = mutableListOf<String>()

        for (word in words) {
            if (currentLine.length + word.length > maxWidth) {
                lines.add(currentLine.trim())
                currentLine = "$word "
            } else {
                currentLine += "$word "
            }
        }

        if (currentLine.isNotBlank()) {
            lines.add(currentLine.trim())
        }

        return lines.joinToString("\n") { it.center(maxWidth) }
    }

    private fun String.center(length: Int, padChar: Char = ' '): String {
        val padding = max(0, length - this.length)
        val padStart = padding / 2
        val padEnd = padding - padStart
        return padChar.toString().repeat(padStart) + this + padChar.toString().repeat(padEnd)
    }
}