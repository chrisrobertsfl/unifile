package com.ingenifi.unifile.content.conversion

import com.ingenifi.unifile.content.Content
import com.ingenifi.unifile.content.ContentType
import com.ingenifi.unifile.content.KeywordExtractor
import java.io.File

data class Json(private val keywordExtractor: KeywordExtractor, private val type: ContentType = ContentType.JSON) : ContentConverter {
    override fun convert(file: File): List<Content> {
        val jsonString = file.readText()
        val filteredJsonString = removeNullFields(jsonString)

        val extracted = keywordExtractor.extract(filteredJsonString)
            .asSequence()
            .filterNot {  it.length > 25 }
            .filterNot {  it.contains("customfield") }
            .filterNot { it.matches("^[0-9].*".toRegex()) }
            .filterNot {  it.contains("lastviewed") }
            .filterNot {  it.contains("issuelink") }
            .filterNot {  it.contains("https") }
            .filterNot {  it.contains("clone") }
            .filterNot {  it.contains("assignee") }
            .filterNot {  it.contains("subtask") }
            .filterNot {  it.contains("comatlassian") }
            .filterNot {  it.contains("issuetype") }
            .filterNot {  it.contains("jirauser") }
            .toList()
        return listOf(Content(type = type, source = file.name, keywords = extracted + file.keywords(), body = filteredJsonString))
    }

    private fun removeNullFields(jsonString: String): String {
        val lines = jsonString.lines()
        val filteredLines = lines.filter { !it.contains(": null,") && !it.endsWith(": null") }
        return filteredLines.joinToString("\n")
    }
}
