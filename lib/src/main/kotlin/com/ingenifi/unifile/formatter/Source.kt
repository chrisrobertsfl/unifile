package com.ingenifi.unifile.formatter

import com.ingenifi.unifile.formatter.jira.*
import com.ingenifi.unifile.formatter.toc.Entry
import com.ingenifi.unifile.formatter.toc.HeadingNumber
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

interface EntrySource : Source {
    val headingNumber: HeadingNumber
    fun entry(): Entry
}

data class IssueSource(val issue: Issue, override val headingNumber: HeadingNumber) : EntrySource {

    override fun entry(): Entry {
        val prefix = when (issue) {
            is Epic -> "Epic"
            is Story -> "Story"
            is Spike -> "Spike"
            is Bug -> "Bug"
        }
        return Entry(prefix = prefix, headingNumber = headingNumber, title = title())
    }
    override fun description(): String = issue.description
    override fun title(): String = "${issue.key} - ${issue.title}"
}