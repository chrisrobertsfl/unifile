package com.ingenifi.unifile.formatter.pdf

import com.ingenifi.unifile.formatter.Delegate
import com.ingenifi.unifile.formatter.DocumentFormatter
import com.ingenifi.unifile.formatter.KeywordExtractor
import com.ingenifi.unifile.formatter.toc.TableOfContents
import java.io.File

data class PdfFormatter(val file: File, private val keywordExtractor: KeywordExtractor, val toc: TableOfContents) : DocumentFormatter {
    private val delegate = Delegate(PdfSource(file), keywordExtractor, toc)
    override fun format(number: Int): String = delegate.format(number, templatePath = "pdf-document.tmpl")
    override fun lastNumber(): Int = delegate.lastNumber()
}

