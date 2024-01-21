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

data class EpicStoryFormatter(
    private val story: Story, private val epic: Epic, private val childNumber: Int, val keywordExtractor: KeywordExtractor, private val verbosity: Verbosity, val toc: TableOfContents
) : DocumentFormatter, VerbosePrinting by VerbosePrinter(verbosity) {

    private var lastNumber = 0
    override fun format(number: Int): String {
        val source = IssueSource(issue = story, sectionNumber = SectionNumber(number, childNumber))
        val delegate = Delegate(source, keywordExtractor, toc)
        verbosePrint("Processing child story '${source.entry()}'")
        lastNumber = number
        return delegate.format(
            number, templatePath = "epic-story-document.tmpl", replacements = replacements(), additionalKeywords = additionalKeywords()
        )
    }

    private fun replacements() = mapOf("epicKey" to epic.key, "epicTitle" to epic.title)

    private fun additionalKeywords(): MutableList<String> {
        val keywords = mutableListOf<String>()
        keywords.add("child")
        keywords.add("story")
        keywords.add(story.key)
        keywords.add(epic.title)
        return keywords
    }


    override fun lastNumber(): Int = lastNumber + 1
}