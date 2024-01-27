package com.ingenifi.unifile.formatter.plaintext

import com.ingenifi.unifile.formatter.Delegate
import com.ingenifi.unifile.formatter.DocumentFormatter
import com.ingenifi.unifile.model.generators.KeywordExtractor
import com.ingenifi.unifile.formatter.toc.TableOfContents
import java.io.File

data class PlainTextFormatter(val file: File, private val keywordExtractor: KeywordExtractor, val toc: TableOfContents) : DocumentFormatter {
    private val delegate = Delegate(PlainTextSource(file), keywordExtractor, toc)
    override fun format(number: Int): String = delegate.format(number, templatePath = "plain-document.tmpl")
    override fun lastNumber(): Int = delegate.lastNumber()
}