package com.ingenifi.unifile.content

import kotlinx.serialization.Serializable

@Serializable
data class Content(val type: ContentType, val source: String, val keywords: List<String>, val body: String)