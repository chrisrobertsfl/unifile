package com.ingenifi.unifile.formatter.jira

import com.ingenifi.unifile.VerbosePrinter
import com.ingenifi.unifile.VerbosePrinting
import com.ingenifi.unifile.Verbosity
import com.ingenifi.unifile.formatter.Delegate
import com.ingenifi.unifile.formatter.DocumentFormatter
import com.ingenifi.unifile.formatter.IssueSource
import com.ingenifi.unifile.formatter.KeywordExtractor
import com.ingenifi.unifile.formatter.toc.DocumentNumber
import com.ingenifi.unifile.formatter.toc.SectionNumber
import com.ingenifi.unifile.formatter.toc.TableOfContents

data class SpikeFormatter(private val spike: Spike, val keywordExtractor: KeywordExtractor, val toc : TableOfContents, private val verbosity: Verbosity) : DocumentFormatter, VerbosePrinting by VerbosePrinter(verbosity) {


    private var lastNumber = 0

    override fun format(number: Int): String {
        val source = IssueSource(issue = spike, headingNumber = DocumentNumber(number))
        val delegate = Delegate(source, keywordExtractor, toc)

        verbosePrint("Processing spike ${spike.key}: '${spike.title}'")

        lastNumber = number
        val keywords = mutableListOf<String>()
        keywords.add("spike")
        keywords.add(spike.key)
        return delegate.format(
            number,
            templatePath = "story-document.tmpl",
            additionalKeywords = keywords
        )
    }

    override fun lastNumber(): Int = lastNumber + 1
}