package com.ingenifi.unifile.formatter.powerpoint

import com.ingenifi.unifile.formatter.Delegate
import com.ingenifi.unifile.formatter.DocumentFormatter
import com.ingenifi.unifile.formatter.KeywordExtractor
import com.ingenifi.unifile.formatter.toc.TableOfContents
import java.io.File

data class PowerPointFormatter(val file: File, private val keywordExtractor: KeywordExtractor, val toc : TableOfContents) : DocumentFormatter {
    private val delegate = Delegate(PowerPointSource(file), keywordExtractor, toc)
    override fun format(number: Int): String = delegate.format(
        number,
        templatePath = "powerpoint-document.tmpl",
        replacements = mapOf<String, String>("filename" to file.name))
    override fun lastNumber(): Int = delegate.lastNumber()
}

