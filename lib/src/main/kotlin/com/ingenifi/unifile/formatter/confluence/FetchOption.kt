package com.ingenifi.unifile.formatter.confluence

data class FetchOption(
    val key: String, val path: String, val interpretAsHtml: Boolean = false
)