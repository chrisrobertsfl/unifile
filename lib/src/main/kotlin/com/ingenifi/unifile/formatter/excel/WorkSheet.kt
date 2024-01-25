package com.ingenifi.unifile.formatter.excel

data class WorkSheet(
    val workbookName: String,
    val title: String,
    val header: List<String>,
    val body: String
) {
    fun toFullCsv(): String {
        val headerString = header.joinToString(",") { "\"$it\"" }
        return "$headerString\n$body"
    }
}

