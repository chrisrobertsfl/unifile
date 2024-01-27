package com.ingenifi.unifile.formatter.jira

import com.ingenifi.unifile.ParameterStore
import com.ingenifi.unifile.VerbosePrinter
import com.ingenifi.unifile.VerbosePrinting
import com.ingenifi.unifile.Verbosity
import com.ingenifi.unifile.formatter.DocumentFormatter
import com.ingenifi.unifile.KeywordExtractor
import com.ingenifi.unifile.formatter.toc.TableOfContents
import io.ktor.client.*
import java.io.File

data class JiraFormatter(
    val parameterStore: ParameterStore,
    val file: File,
    val client: HttpClient,
    val keywordExtractor: KeywordExtractor,
    private val verbosity: Verbosity,
    val toc: TableOfContents
) : DocumentFormatter,
    VerbosePrinting by VerbosePrinter(verbosity) {
    private val jiraBaseUrl = parameterStore.getParameter("jiraBaseUrl")
    private val apiToken = parameterStore.getParameter("apiToken")
    private val api = JiraApi(client = client, jiraBaseUrl = jiraBaseUrl, apiToken = apiToken)

    private var lastNumber = 0
    private val factory = IssueFactory(api = api, verbosity = verbosity.increasingBy(1))
    private val issueVerbosity = factory.verbosity.increasingBy(1)
    private val documents = mutableListOf<String>()
    private val keys = IssueKey.parse(file)

    override fun format(number: Int): String {
        lastNumber = number
        for (key in keys) {
            val issue = factory.create(key)
            documents.add(formatIssue(issue))
        }
        return documents.joinToString("\n")
    }

    private fun formatIssue(issue: Issue): String = when (issue) {
        is Epic -> EpicFormatter(epic = issue, keywordExtractor = keywordExtractor, toc = toc, verbosity = issueVerbosity).format(lastNumber++)
        is Story -> StoryFormatter(story = issue, keywordExtractor = keywordExtractor, toc = toc, verbosity = issueVerbosity).format(lastNumber++)
        is Spike -> SpikeFormatter(spike = issue, keywordExtractor = keywordExtractor, toc = toc, verbosity = issueVerbosity).format(lastNumber++)
        is Bug -> BugFormatter(bug = issue, keywordExtractor = keywordExtractor, toc = toc, verbosity = issueVerbosity).format(lastNumber++)
    }

    override fun lastNumber(): Int = lastNumber
}

data class IssueKey(val key: String, val additionalKeywords: List<String> = listOf()) {
    companion object {
        fun parse(file: File): List<IssueKey> {
            return file.readLines().map { line ->
                val parts = line.split(":")
                val key = parts[0]
                val additionalKeywords = if (parts.size > 1) {
                    parts[1].split(",").filter { it.isNotBlank() }.map { it.trim() }
                } else {
                    listOf()
                }
                IssueKey(key, additionalKeywords)
            }
        }
    }
}

