package com.ingenifi.unifile.model.generators

import com.ingenifi.unifile.ParameterStore
import com.ingenifi.unifile.ParameterStore.Companion.NONE
import com.ingenifi.unifile.UnsecuredHttpClient.create
import com.ingenifi.unifile.Verbosity
import io.ktor.client.*


data class SectionGeneratorConfig(
    val keywordExtractor: KeywordExtractor = KeywordExtractor(), val verbosity: Verbosity = Verbosity.NONE, val client: HttpClient = create(), val parameterStore: ParameterStore = NONE, val additionalKeywords: List<String> = listOf()
)