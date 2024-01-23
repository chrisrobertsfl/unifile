package com.ingenifi.unifile.formatter

import com.ingenifi.unifile.formatter.jira.Issue
import com.ingenifi.unifile.formatter.toc.Entry
import com.ingenifi.unifile.formatter.toc.HeadingNumber
import com.ingenifi.unifile.formatter.toc.SectionNumber
import java.io.File

interface Source {
    fun description(): String
    fun title(): String
}

interface FileSource : Source {
    val file: File

    fun extractKeywords(): List<String> {
        val pathKeywords = file.toPath().parent?.iterator()?.asSequence()?.map { it.toString().replace(Regex("\\W+"), " ").trim() }?.filter { it.isNotEmpty() }?.toList() ?: emptyList()
        val normalizedFileName = file.name.substringBeforeLast('.').replace(Regex("\\W+"), " ").trim()
        val keywords = pathKeywords + listOf(normalizedFileName)
        val extension = file.extension
        return if (extension.isNotEmpty()) keywords + extension else keywords
    }

    override fun title(): String = "File name: ${file.name}"
}

data class IssueSource(val issue: Issue, val headingNumber : HeadingNumber) : Source {
    override fun description(): String = issue.description
    override fun title(): String = "${issue.key} - ${issue.title}"
    fun entry() : Entry = Entry(headingNumber = headingNumber, title = title())
}