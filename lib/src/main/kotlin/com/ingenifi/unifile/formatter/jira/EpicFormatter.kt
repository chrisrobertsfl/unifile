package com.ingenifi.unifile.formatter.jira

import com.ingenifi.unifile.formatter.Delegate
import com.ingenifi.unifile.formatter.DocumentFormatter
import com.ingenifi.unifile.formatter.KeywordExtractor

data class EpicFormatter(private val epic: Epic, val keywordExtractor: KeywordExtractor) : DocumentFormatter {

    private val delegate = Delegate(EpicSource(epic), keywordExtractor)

    private var lastNumber = 0

    override fun format(number: Int): String {
        val children = epic.children.mapIndexed { index, issue ->
            when (issue) {
                is Story -> EpicStoryFormatter(issue, epic, index + 1, keywordExtractor).format(number)
                is Spike -> EpicSpikeFormatter(issue, epic, index + 1, keywordExtractor).format(number)
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