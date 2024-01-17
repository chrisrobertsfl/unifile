package com.ingenifi.unifile.formatter.confluence

import com.ingenifi.unifile.formatter.Delegate
import com.ingenifi.unifile.formatter.DocumentFormatter
import com.ingenifi.unifile.formatter.KeywordExtractor

data class ConfluencePageFormatter(private val link : ConfluenceLink, private val keywordExtractor: KeywordExtractor) : DocumentFormatter {
    private val delegate = Delegate(link, keywordExtractor)
    override fun format(number: Int): String  = delegate.format(number,  "confluence-document.tmpl",  extractPercentage = 0.1)
    override fun lastNumber(): Int = delegate.lastNumber()
}