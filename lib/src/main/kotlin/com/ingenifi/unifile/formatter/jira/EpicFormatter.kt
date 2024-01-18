package com.ingenifi.unifile.formatter.jira

import com.ingenifi.unifile.VerbosePrinter
import com.ingenifi.unifile.VerbosePrinting
import com.ingenifi.unifile.Verbosity
import com.ingenifi.unifile.formatter.Delegate
import com.ingenifi.unifile.formatter.DocumentFormatter
import com.ingenifi.unifile.formatter.KeywordExtractor

data class EpicFormatter(private val epic: Epic, val keywordExtractor: KeywordExtractor, private val verbosity: Verbosity) : DocumentFormatter, VerbosePrinting by VerbosePrinter(verbosity) {

    private val delegate = Delegate(EpicSource(epic), keywordExtractor)

    private var lastNumber = 0

    override fun format(number: Int): String {
        verbosePrint("Processing epic ${epic.key}: '${epic.title}' with ${epic.children.size} children")
        val childVerbosity = verbosity.increasingBy(1)
        val children = epic.children.mapIndexed { index, issue ->
            when (issue) {
                is Story -> EpicStoryFormatter(story = issue, epic, childNumber = index + 1, keywordExtractor, verbosity = childVerbosity).format(number)
                is Spike -> EpicSpikeFormatter(spike = issue, epic, childNumber = index + 1, keywordExtractor, verbosity = childVerbosity).format(number)
                else -> ""
            }
        }.joinToString("\n")
        val keywords = mutableListOf<String>()
        keywords.add("parent")
        keywords.add("epic")
        keywords.add(epic.key)
        keywords.add(epic.title)
        return delegate.format(
            number,
            templatePath = "epic-document.tmpl",
            replacements = mapOf<String, String>("key" to epic.key, "title" to epic.title, "introduction" to epic.introduction, "children" to children),
            additionalKeywords = keywords
        )
    }

    override fun lastNumber(): Int {
        TODO("Not yet implemented")
    }
}