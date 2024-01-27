package com.ingenifi.unifile.model.document

import com.ingenifi.unifile.Verbosity
import com.ingenifi.unifile.formatter.KeywordExtractor

interface SectionGenerator {
    fun generate() = listOf<Section>()

    data class Config(val keywordExtractor: KeywordExtractor, val verbosity: Verbosity)
}