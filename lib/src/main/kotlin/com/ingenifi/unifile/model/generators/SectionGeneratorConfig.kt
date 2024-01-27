package com.ingenifi.unifile.model.generators

import com.ingenifi.unifile.ParameterStore
import com.ingenifi.unifile.UnsecuredHttpClient
import com.ingenifi.unifile.verbosity.Verbosity
import io.ktor.client.*

data class SectionGeneratorConfig(val keywordExtractor: KeywordExtractor, val verbosity: Verbosity, val client: HttpClient = UnsecuredHttpClient.create(), val parameterStore: ParameterStore = ParameterStore.NONE)