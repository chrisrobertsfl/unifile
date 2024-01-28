package com.ingenifi.unifile.model.generators.jira

import com.ingenifi.unifile.model.generators.KeywordExtractor
import com.ingenifi.unifile.model.document.KeywordsText
import com.ingenifi.unifile.model.document.SummaryText

interface ChildCreator {
    val epicChild: EpicChild
    val keywordExtractor: KeywordExtractor

    fun createKeywords(): KeywordsText.Keywords {
        val detailKeywords = keywordExtractor.extract(epicChild.detail)
        val additionalKeywords = listOf("child", epicChild.key, epicChild.epic.key)
        return KeywordsText.Keywords(detailKeywords + additionalKeywords)
    }

    fun createSummary(): SummaryText {
        val content = "This is a child '${epicChild.key} - ${epicChild.title}' of epic '${epicChild.epic.key} - ${epicChild.epic.title}'"
        return SummaryText.Summary(content)
    }
}