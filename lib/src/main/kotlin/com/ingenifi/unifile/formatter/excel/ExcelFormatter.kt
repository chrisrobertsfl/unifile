package com.ingenifi.unifile.formatter.excel

import com.ingenifi.unifile.formatter.Delegate
import com.ingenifi.unifile.formatter.DocumentFormatter
import com.ingenifi.unifile.formatter.KeywordExtractor
import com.ingenifi.unifile.formatter.toc.TableOfContents
import java.io.File

data class ExcelFormatter(val file: File, private val keywordExtractor: KeywordExtractor, private val toc : TableOfContents) : DocumentFormatter {
    private val delegate = Delegate(ExcelSource(file), keywordExtractor, toc)
    override fun format(number: Int): String = delegate.format(number, templatePath = "excel-document.tmpl")
    override fun lastNumber(): Int = delegate.lastNumber()
}
