package com.ingenifi.unifile.formatter.word

import com.ingenifi.unifile.formatter.Delegate
import com.ingenifi.unifile.formatter.DocumentFormatter
import com.ingenifi.unifile.KeywordExtractor
import com.ingenifi.unifile.formatter.toc.TableOfContents
import java.io.File

data class WordFormatter(val file: File, private val keywordExtractor: KeywordExtractor, val toc : TableOfContents) : DocumentFormatter {
    private val delegate = Delegate(WordSource(file), keywordExtractor, toc)
    override fun format(number: Int): String = delegate.format(number, templatePath = "word-document.tmpl")
    override fun lastNumber(): Int = delegate.lastNumber()
}

