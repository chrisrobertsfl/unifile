package com.ingenifi.unifile.formatter.toc

import java.lang.IllegalStateException

data class TableOfContents(private val entries: MutableList<Entry> = mutableListOf()) {
    fun format(formatter: TableOfContentsFormatter = JustifiedTableOfContentsFormatter()): String = formatter.format(entries)
    fun addEntry(sectionNumber: SectionNumber, title: String) = addEntry(Entry(sectionNumber, title))
    fun addEntry(title: String, vararg levels: Int) = addEntry(SectionNumber(levels.toList()), title)
    fun addEntry( entry : Entry) {
        if (entries.contains(entry)) {
            throw IllegalStateException("Entry -> $entry already exists")
        }
        entries.add(entry)
    }
    fun hasEntries(): Boolean = entries.isNotEmpty()
}