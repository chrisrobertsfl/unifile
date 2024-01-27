package com.ingenifi.unifile.model.document

data class FetchOption(
    val key: String, val path: String, val interpretAsHtml: Boolean = false
)