package com.ingenifi.unifile.model.generators.confluence

data class FetchOption(
    val key: String, val path: String, val interpretAsHtml: Boolean = false
)