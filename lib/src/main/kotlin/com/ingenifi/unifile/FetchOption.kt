package com.ingenifi.unifile

data class FetchOption(
    val key: String, val path: String, val interpretAsHtml: Boolean = false
)