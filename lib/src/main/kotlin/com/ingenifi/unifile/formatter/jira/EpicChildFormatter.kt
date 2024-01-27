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

data class EpicChildFormatter(
    private val issue: Issue,
    private val epic: Epic,
    private val childNumber: Int,
    val keywordExtractor: KeywordExtractor,
    val toc: TableOfContents,
    private val verbosity: Verbosity,
    val templatePath: String,
    val additionalKeywords: List<String>
) : DocumentFormatter, VerbosePrinting by VerbosePrinter(verbosity) {

    private var lastNumber = 0
    override fun format(number: Int): String {
        val source = IssueSource(issue = issue, headingNumber = HeadingNumber(listOf(number, childNumber)))
        val delegate = Delegate(source, keywordExtractor, toc)
        verbosePrint("Processing child story '${issue.key} - ${issue.title}'")
        lastNumber = number
        return delegate.format(
            number, templatePath = templatePath, replacements = replacements(source), additionalKeywords = additionalKeywords()
        )
    }

    private fun replacements(source : IssueSource) = mapOf<String, String>("headingNumber" to source.headingNumber.asString(), "epicKey" to epic.key, "epicTitle" to epic.title)

    private fun additionalKeywords(): MutableList<String> {
        val keywords = mutableListOf<String>()
        keywords.addAll(additionalKeywords)
        keywords.add("child")
        keywords.add(issue.key)
        keywords.add(epic.title)
        return keywords
    }

    override fun lastNumber(): Int = lastNumber + 1
}