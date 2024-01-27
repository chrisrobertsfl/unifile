package com.ingenifi.unifile.formatter.properites

import com.ingenifi.unifile.formatter.Delegate
import com.ingenifi.unifile.formatter.DocumentFormatter
import com.ingenifi.unifile.KeywordExtractor
import com.ingenifi.unifile.formatter.plaintext.PlainTextSource
import com.ingenifi.unifile.formatter.toc.TableOfContents
import java.io.File

data class TemplateFormatter(val file: File, private val keywordExtractor: KeywordExtractor, val toc: TableOfContents) : DocumentFormatter {
    private val delegate = Delegate(PlainTextSource(file), keywordExtractor, toc)
    override fun format(number: Int): String = delegate.format(number, templatePath = "tmpl-document.tmpl")
    override fun lastNumber(): Int = delegate.lastNumber()
}