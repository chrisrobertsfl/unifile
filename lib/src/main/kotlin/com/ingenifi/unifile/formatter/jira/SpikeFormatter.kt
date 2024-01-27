package com.ingenifi.unifile.formatter.jira

import com.ingenifi.unifile.verbosity.VerbosePrinter
import com.ingenifi.unifile.verbosity.VerbosePrinting
import com.ingenifi.unifile.verbosity.Verbosity
import com.ingenifi.unifile.formatter.Delegate
import com.ingenifi.unifile.formatter.DocumentFormatter
import com.ingenifi.unifile.formatter.IssueSource
import com.ingenifi.unifile.model.generators.KeywordExtractor
import com.ingenifi.unifile.formatter.toc.HeadingNumber

import com.ingenifi.unifile.formatter.toc.TableOfContents

data class SpikeFormatter(private val spike: Spike, val keywordExtractor: KeywordExtractor, val toc : TableOfContents, private val verbosity: Verbosity) : DocumentFormatter, VerbosePrinting by VerbosePrinter(verbosity) {


    private var lastNumber = 0

    override fun format(number: Int): String {
        val source = IssueSource(issue = spike, headingNumber = HeadingNumber(listOf(number)))
        val delegate = Delegate(source, keywordExtractor, toc)

        verbosePrint("Processing spike ${spike.key}: '${spike.title}'")

        lastNumber = number
        val keywords = mutableListOf<String>()
        keywords.add("spike")
        keywords.add(spike.key)
        keywords.addAll(spike.additionalKeywords)

        return delegate.format(
            number,
            templatePath = "story-document.tmpl",
            additionalKeywords = keywords
        )
    }

    override fun lastNumber(): Int = lastNumber + 1
}