package com.ingenifi.unifile.model.generators.jira

import com.ingenifi.unifile.model.generators.KeywordExtractor
import com.ingenifi.unifile.model.document.KeywordsText
import com.ingenifi.unifile.model.document.SummaryText

interface ChildCreator {
    val jiraIssue: EpicChild
    val keywordExtractor: KeywordExtractor

    fun createKeywords(): KeywordsText.Keywords {
        val detailKeywords = keywordExtractor.extract(jiraIssue.detail)
        val additionalKeywords = listOf("child", jiraIssue.key, jiraIssue.epic.key)
        return KeywordsText.Keywords(detailKeywords + additionalKeywords)
    }

    fun createSummary(): SummaryText {
        val content = "This is a child '${jiraIssue.key} - ${jiraIssue.title}' of epic '${jiraIssue.epic.key} - ${jiraIssue.epic.title}'"
        return SummaryText.Summary(content)
    }
}