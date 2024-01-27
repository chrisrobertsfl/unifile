package com.ingenifi.unifile.model.generators.jira

import com.ingenifi.unifile.model.generators.KeywordExtractor

data class DefaultChildCreator(
    override val jiraIssue: EpicChild, override val keywordExtractor: KeywordExtractor
) : ChildCreator