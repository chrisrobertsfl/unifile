package com.ingenifi.unifile.content.formatter

import com.ingenifi.unifile.content.KeywordExtractor
import java.io.File

data class PlainTextFormatter(private val file: File, private val keywordExtractor: KeywordExtractor) : DocumentFormatter {
    private val delegate = Delegate(FileSource(file), keywordExtractor)
    override fun format(number: Int): String = delegate.format(number,  "plain-document.tmpl")
    override fun lastNumber(): Int = delegate.lastNumber()
}