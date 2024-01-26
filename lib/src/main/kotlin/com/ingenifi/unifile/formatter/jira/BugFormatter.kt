package com.ingenifi.unifile.formatter.jira

import com.ingenifi.unifile.VerbosePrinter
import com.ingenifi.unifile.VerbosePrinting
import com.ingenifi.unifile.Verbosity
import com.ingenifi.unifile.formatter.Delegate
import com.ingenifi.unifile.formatter.DocumentFormatter
import com.ingenifi.unifile.formatter.IssueSource
import com.ingenifi.unifile.formatter.KeywordExtractor
import com.ingenifi.unifile.formatter.toc.HeadingNumber
import com.ingenifi.unifile.formatter.toc.TableOfContents

data class BugFormatter(private val bug: Bug, val keywordExtractor: KeywordExtractor, val toc: TableOfContents, private val verbosity: Verbosity) : DocumentFormatter,
    VerbosePrinting by VerbosePrinter(verbosity) {


    private var lastNumber = 0

    override fun format(number: Int): String {
        val source = IssueSource(issue = bug, headingNumber = HeadingNumber(listOf(number)))
        val delegate = Delegate(source, keywordExtractor, toc)

        verbosePrint("Processing bug ${bug.key}: '${bug.title}'")

        lastNumber = number
        val keywords = mutableListOf<String>()
        keywords.add("bug")
        keywords.add(bug.key)
        keywords.addAll(bug.additionalKeywords)
        return delegate.format(
            number, templatePath = "bug-document.tmpl", additionalKeywords = keywords
        )
    }

    override fun lastNumber(): Int = lastNumber + 1
}