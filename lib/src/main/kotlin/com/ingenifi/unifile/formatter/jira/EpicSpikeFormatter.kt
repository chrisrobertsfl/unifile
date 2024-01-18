package com.ingenifi.unifile.formatter.jira

import com.ingenifi.unifile.VerbosePrinter
import com.ingenifi.unifile.VerbosePrinting
import com.ingenifi.unifile.Verbosity
import com.ingenifi.unifile.formatter.Delegate
import com.ingenifi.unifile.formatter.DocumentFormatter
import com.ingenifi.unifile.formatter.KeywordExtractor

data class EpicSpikeFormatter(private val spike: Spike, private val epic: Epic, private val childNumber: Int, val keywordExtractor: KeywordExtractor, private val verbosity: Verbosity) :
    DocumentFormatter, VerbosePrinting by VerbosePrinter(verbosity) {

    private val delegate = Delegate(SpikeSource(spike), keywordExtractor)

    private var lastNumber = 0
    override fun format(number: Int): String {
        verbosePrint("Processing child story ${spike.key}: '${spike.title}'")
        lastNumber = number
        val keywords = mutableListOf<String>()
        keywords.add("child")
        keywords.add("spike")
        keywords.add(spike.key)
        keywords.add(epic.title)
        return delegate.format(
            number, templatePath = "epic-spike-document.tmpl", replacements = mapOf<String, String>(
                "epicKey" to epic.key, "epicTitle" to epic.title, "key" to spike.key, "title" to spike.title, "epicNumber" to number.toString(), "number" to childNumber.toString()
            ), additionalKeywords = keywords
        )

    }

    override fun lastNumber(): Int = lastNumber + 1
}