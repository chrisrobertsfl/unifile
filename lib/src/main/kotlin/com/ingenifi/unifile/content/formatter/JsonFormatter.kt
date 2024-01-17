package com.ingenifi.unifile.content.formatter

import com.ingenifi.unifile.content.KeywordExtractor
import java.io.File

data class JsonFormatter(val file: File, private val keywordExtractor: KeywordExtractor) : DocumentFormatter {
    private val delegate = Delegate(JsonSource(file), keywordExtractor)
    override fun format(number: Int): String = delegate.format(number,  templatePath = "json-document.tmpl", replacements = mapOf<String, String>( "filename" to file.name))
    override fun lastNumber(): Int = delegate.lastNumber()
}
