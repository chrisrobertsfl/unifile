package com.ingenifi.unifile.model.document

import com.ingenifi.unifile.ParameterStore
import com.ingenifi.unifile.UnsecuredHttpClient
import com.ingenifi.unifile.Verbosity
import com.ingenifi.unifile.KeywordExtractor
import io.ktor.client.*

interface SectionGenerator {
    fun generate() = listOf<Section>()

    data class Config(val keywordExtractor: KeywordExtractor, val verbosity: Verbosity, val client: HttpClient = UnsecuredHttpClient.create(), val parameterStore: ParameterStore = ParameterStore.NONE)
}