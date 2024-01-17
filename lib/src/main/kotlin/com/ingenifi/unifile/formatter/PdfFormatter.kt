package com.ingenifi.unifile.formatter

import java.io.File

data class PdfFormatter(val file: File, private val keywordExtractor: KeywordExtractor) : DocumentFormatter {
    private val delegate = Delegate(PdfSource(file), keywordExtractor)
    override fun format(number: Int): String = delegate.format(number, templatePath = "pdf-document.tmpl", replacements = mapOf<String, String>("filename" to file.name))
    override fun lastNumber(): Int = delegate.lastNumber()
}

