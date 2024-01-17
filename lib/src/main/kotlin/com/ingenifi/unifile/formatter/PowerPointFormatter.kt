package com.ingenifi.unifile.formatter

import java.io.File

data class PowerPointFormatter(val file: File, private val keywordExtractor: KeywordExtractor) : DocumentFormatter {
    private val delegate = Delegate(PowerPointSource(file), keywordExtractor)
    override fun format(number: Int): String = delegate.format(number, templatePath = "powerpoint-document.tmpl", replacements = mapOf<String, String>("filename" to file.name))
    override fun lastNumber(): Int = delegate.lastNumber()
}

