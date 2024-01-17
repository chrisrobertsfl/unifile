package com.ingenifi.unifile.content

import kotlinx.serialization.Serializable


sealed interface Content
@Serializable
data class JsonContent(val type: ContentType, val source: String, val keywords: List<String>, val body: String) : Content


data class PlainContent(private val body : String) : Content