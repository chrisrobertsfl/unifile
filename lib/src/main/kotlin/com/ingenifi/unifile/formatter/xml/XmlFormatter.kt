package com.ingenifi.unifile.formatter.xml

import com.ingenifi.unifile.formatter.Delegate
import com.ingenifi.unifile.formatter.DocumentFormatter
import com.ingenifi.unifile.KeywordExtractor
import com.ingenifi.unifile.formatter.toc.TableOfContents
import java.io.File

data class XmlFormatter(private val file: File, private val keywordExtractor: KeywordExtractor, val toc: TableOfContents) : DocumentFormatter {
    private val delegate = Delegate(XmlSource(file), keywordExtractor, toc)

    override fun format(number: Int): String = delegate.format(number, "xml-document.tmpl")

    override fun lastNumber(): Int = delegate.lastNumber()

}