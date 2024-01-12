package com.ingenifi.unifile

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json


@Serializable
data class Files(val fileEntries : List<FileEntry>) {
    fun toJsonString(): String {
        val prettyJson = Json { prettyPrint = true }
        return prettyJson.encodeToString(serializer(), this)
    }

}

@Serializable
data class FileEntry(val fileName : String, val keywords : List<String>, val contents : String) {

}