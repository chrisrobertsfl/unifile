package com.ingenifi.unifile.formatter.jira

import com.ingenifi.unifile.VerbosePrinter
import com.ingenifi.unifile.VerbosePrinting
import com.ingenifi.unifile.Verbosity
import com.ingenifi.unifile.formatter.Delegate
import com.ingenifi.unifile.formatter.DocumentFormatter
import com.ingenifi.unifile.formatter.KeywordExtractor

data class SpikeFormatter(private val spike: Spike, val keywordExtractor: KeywordExtractor, private val verbosity: Verbosity) : DocumentFormatter, VerbosePrinting by VerbosePrinter(verbosity) {

    private val delegate = Delegate(SpikeSource(spike), keywordExtractor)

    private var lastNumber = 0

    override fun format(number: Int): String {
        verbosePrint("Processing spike ${spike.key}: '${spike.title}'")

        lastNumber = number
        val keywords = mutableListOf<String>()
        keywords.add("spike")
        keywords.add(spike.key)
        return delegate.format(number, templatePath = "story-document.tmpl", replacements = mapOf<String, String>("key" to spike.key, "title" to spike.title), additionalKeywords = keywords)
    }

    override fun lastNumber(): Int = lastNumber + 1
}