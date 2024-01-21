package com.ingenifi.unifile.formatter.json

import com.ingenifi.unifile.formatter.Delegate
import com.ingenifi.unifile.formatter.DocumentFormatter
import com.ingenifi.unifile.formatter.KeywordExtractor
import com.ingenifi.unifile.formatter.toc.TableOfContents
import java.io.File

data class JsonFormatter(val file: File, private val keywordExtractor: KeywordExtractor, val toc: TableOfContents) : DocumentFormatter {
    private val delegate = Delegate(JsonSource(file), keywordExtractor, toc)
    override fun format(number: Int): String = delegate.format(number, templatePath = "json-document.tmpl")
    override fun lastNumber(): Int = delegate.lastNumber()
}

