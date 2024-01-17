package com.ingenifi.unifile.formatter.jira

import com.ingenifi.unifile.formatter.Delegate
import com.ingenifi.unifile.formatter.DocumentFormatter
import com.ingenifi.unifile.formatter.KeywordExtractor

data class SpikeFormatter(private val spike: Spike, val keywordExtractor: KeywordExtractor) : DocumentFormatter {
    private val delegate = Delegate(SpikeSource(spike), keywordExtractor)

    private var lastNumber = 0

    override fun format(number: Int): String {
        lastNumber = number
        val keywords = mutableListOf<String>()
        keywords.add("spike")
        keywords.add(spike.key)
        return delegate.format(number, templatePath = "story-document.tmpl", replacements = mapOf<String, String>("key" to spike.key, "title" to spike.title), additionalKeywords = keywords)
    }

    override fun lastNumber(): Int = lastNumber + 1
}