package com.ingenifi.unifile.model.document

import com.ingenifi.unifile.KeywordExtractor

data class DefaultChildCreator(
    override val jiraIssue: EpicChild, override val keywordExtractor: KeywordExtractor
) : ChildCreator