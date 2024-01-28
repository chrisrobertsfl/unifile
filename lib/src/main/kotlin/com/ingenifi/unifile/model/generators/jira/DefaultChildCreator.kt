package com.ingenifi.unifile.model.generators.jira

import com.ingenifi.unifile.model.generators.KeywordExtractor

data class DefaultChildCreator(
    override val epicChild: EpicChild, override val keywordExtractor: KeywordExtractor
) : ChildCreator