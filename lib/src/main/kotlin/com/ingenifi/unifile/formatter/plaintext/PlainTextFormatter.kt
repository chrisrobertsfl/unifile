package com.ingenifi.unifile.formatter.plaintext

import com.ingenifi.unifile.formatter.Delegate
import com.ingenifi.unifile.formatter.DocumentFormatter
import com.ingenifi.unifile.formatter.KeywordExtractor
import java.io.File

data class PlainTextFormatter(val file: File, private val keywordExtractor: KeywordExtractor) : DocumentFormatter {
    private val delegate = Delegate(PlainTextSource(file), keywordExtractor)
    override fun format(number: Int): String = delegate.format(number,  templatePath = "plain-document.tmpl", replacements = mapOf<String, String>( "filename" to file.name))
    override fun lastNumber(): Int = delegate.lastNumber()
}