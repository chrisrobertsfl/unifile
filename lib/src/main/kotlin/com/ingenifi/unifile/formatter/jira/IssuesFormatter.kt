package com.ingenifi.unifile.formatter.jira

import com.ingenifi.unifile.formatter.DocumentFormatter
import com.ingenifi.unifile.formatter.KeywordExtractor

data class IssuesFormatter(val issues: List<Issue>, val keywordExtractor: KeywordExtractor) : DocumentFormatter {

    private var lastNumber = 0

    override fun format(number: Int): String {
        lastNumber = number
        return issues.map {
            when (it) {
                is Epic -> EpicFormatter(epic = it, keywordExtractor = keywordExtractor).format(lastNumber++)
                is Story -> StoryFormatter(story = it, keywordExtractor = keywordExtractor).format(lastNumber++)
                is Spike -> SpikeFormatter(spike = it, keywordExtractor = keywordExtractor).format(lastNumber++)
            }
        }.joinToString("\n")
    }

    override fun lastNumber(): Int = lastNumber

}