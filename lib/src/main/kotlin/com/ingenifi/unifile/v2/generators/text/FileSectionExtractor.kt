package com.ingenifi.unifile.v2.generators.text

import com.ingenifi.unifile.v2.model.*

data class FileSectionExtractor(private val fileContent: String) {
    private fun extract(tag: String): String = "<!-- $tag -->(.*?)(?=(<!--)|\$)".toRegex(RegexOption.DOT_MATCHES_ALL).find(fileContent)?.groups?.get(1)?.value?.trim() ?: ""
    fun title() = Title(extract("Title"))
    fun summary() = Summary(text = extract("Summary"))
    fun content() = Content(text = extract("Content"))
    fun lastUpdated() = LastUpdated(text = extract("LastUpdated"))
    fun keywords() = Keywords(list = extract("Keywords").split(",").map { it.trim() })
}