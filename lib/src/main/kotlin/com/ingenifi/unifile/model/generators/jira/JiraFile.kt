package com.ingenifi.unifile.model.generators.jira

import java.io.File

data class JiraFile(val inputLines: List<String>) {
    constructor(file: File) : this(inputLines = file.readLines())

    fun lines() = inputLines.map(::asJiraLine).toList()

    companion object {
        private fun asJiraLine(line: String): JiraLine {
            val parts = line.split(":", limit = 2).map(String::trim)
            val key = parts.getOrElse(0) { "" }
            val keywordString = parts.getOrElse(1) { "" }
            val keywords = keywordString.takeIf { it.isNotEmpty() }?.split(",")?.map(String::trim)?.filter(String::isNotEmpty) ?: listOf()
            return JiraLine(key, keywords)
        }

    }
}