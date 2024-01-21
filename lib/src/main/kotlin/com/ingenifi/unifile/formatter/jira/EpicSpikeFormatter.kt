package com.ingenifi.unifile.formatter.jira

import com.ingenifi.unifile.VerbosePrinter
import com.ingenifi.unifile.VerbosePrinting
import com.ingenifi.unifile.Verbosity
import com.ingenifi.unifile.formatter.Delegate
import com.ingenifi.unifile.formatter.DocumentFormatter
import com.ingenifi.unifile.formatter.IssueSource
import com.ingenifi.unifile.formatter.KeywordExtractor
import com.ingenifi.unifile.formatter.toc.SectionNumber
import com.ingenifi.unifile.formatter.toc.TableOfContents

data class EpicSpikeFormatter(
    private val spike: Spike, private val epic: Epic, private val childNumber: Int, val keywordExtractor: KeywordExtractor, val toc: TableOfContents, private val verbosity: Verbosity
) : DocumentFormatter, VerbosePrinting by VerbosePrinter(verbosity) {


    private var lastNumber = 0
    override fun format(number: Int): String {
        val source = IssueSource(issue = spike, sectionNumber = SectionNumber(number, childNumber))
        val delegate = Delegate(source, keywordExtractor, toc)
        verbosePrint("Processing child story '${source.entry()}'")
        lastNumber = number
        return delegate.format(
            number, templatePath = "epic-spike-document.tmpl", replacements = replacements(), additionalKeywords = additionalKeywords()
        )
    }

    private fun replacements() = mapOf<String, String>("epicKey" to epic.key, "epicTitle" to epic.title)

    private fun additionalKeywords(): MutableList<String> {
        val keywords = mutableListOf<String>()
        keywords.add("child")
        keywords.add("spike")
        keywords.add(spike.key)
        keywords.add(epic.title)
        return keywords
    }

    override fun lastNumber(): Int = lastNumber + 1
}