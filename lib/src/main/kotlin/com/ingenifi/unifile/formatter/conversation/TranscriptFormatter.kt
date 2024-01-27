package com.ingenifi.unifile.formatter.conversation

import com.ingenifi.unifile.formatter.Delegate
import com.ingenifi.unifile.formatter.DocumentFormatter
import com.ingenifi.unifile.model.generators.KeywordExtractor
import com.ingenifi.unifile.formatter.toc.TableOfContents
import java.io.File

class TranscriptFormatter(
    private val file: File, private val keywordExtractor: KeywordExtractor, private val toc: TableOfContents
) : DocumentFormatter {

    private var lastNumber = 0
    override fun format(number: Int): String {
        lastNumber = number
        val source = TranscriptSource(file)
        val delegate = Delegate(source, keywordExtractor, toc)
        return delegate.format(number, "conversation-document.tmpl", replacements = replacements(source))
    }

    private fun replacements(source: TranscriptSource) = mapOf("introduction" to source.introduction())
    override fun lastNumber(): Int = lastNumber + 1
}