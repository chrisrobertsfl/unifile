package com.ingenifi.unifile.formatter

import com.ingenifi.unifile.formatter.toc.Entry
import com.ingenifi.unifile.formatter.toc.HeadingNumber
import com.ingenifi.unifile.formatter.toc.TableOfContents

class Delegate(private val source: Source, private val keywordExtractor: KeywordExtractor, private val toc: TableOfContents) {
    private var lastNumber = 0

    fun format(
        documentNumber: Int, templatePath: String, replacements: Map<String, String> = mapOf(), extractPercentage: Double = 0.025, additionalKeywords: List<String> = listOf()
    ): String {
        val description = source.description()
        val title = source.title()
        val keywords = mutableSetOf<String>()
        keywords.addAll(keywordExtractor.extract(text = description, percentage = extractPercentage))
        if (additionalKeywords.isNotEmpty()) keywords.addAll(additionalKeywords)
        if (source is FileSource) {
            keywords.addAll(source.extractKeywords())
        }
        val template = Template(templatePath)

        val replacementsWithDefaults = replacements + mapOf(
            "number" to documentNumber.toString(), "title" to title, "description" to description, "keywords" to keywords.joinToString(", ")
        )
        val formattedContent = template.applyReplacements(replacementsWithDefaults)

        val entry = if (source is EntrySource) source.entry() else Entry(title = title, headingNumber = HeadingNumber(listOf(documentNumber)))
        toc.addEntry(entry)
        lastNumber += 1
        return formattedContent
    }

    fun lastNumber(): Int = lastNumber
}
