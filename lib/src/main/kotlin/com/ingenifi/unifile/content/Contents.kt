package com.ingenifi.unifile.content

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class Contents(val contents: MutableList<Content> = mutableListOf()) {
    fun toJsonString(): String {
        val prettyJson = Json { prettyPrint = true }
        return prettyJson.encodeToString(serializer(), this)
    }

    fun add(contentList: List<Content>) {
        contents.addAll(contentList)
    }

}