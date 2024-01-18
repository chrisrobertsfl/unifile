package com.ingenifi.unifile.formatter.jira

import com.ingenifi.unifile.VerbosePrinter
import com.ingenifi.unifile.VerbosePrinting
import com.ingenifi.unifile.Verbosity
import com.ingenifi.unifile.formatter.DocumentFormatter
import com.ingenifi.unifile.formatter.KeywordExtractor
import io.ktor.client.*
import java.io.File

data class JiraFormatter(val properties : Map<String, String>, val file: File, val client: HttpClient, val keywordExtractor: KeywordExtractor, private val verbosity: Verbosity) : DocumentFormatter,
    VerbosePrinting by VerbosePrinter(verbosity) {
    private val jiraBaseUrl = properties["jiraBaseUrl"] ?: throw IllegalArgumentException("Jira base URL not specified in properties")
    private val apiToken = properties["apiToken"] ?: throw IllegalArgumentException("Jir API Token not specified in properties")
    private val api = JiraApi(client = client, jiraBaseUrl = jiraBaseUrl, apiToken = apiToken)

    private var lastNumber = 0
    private val factory = IssueFactory(api = api, verbosity = verbosity.increasingBy(1))
    private val issueVerbosity = factory.verbosity.increasingBy(1)
    private val documents = mutableListOf<String>()
    private val keys = file.readLines()

    override fun format(number: Int): String {
        for (key in keys) {
            val issue = factory.create(key)
            documents.add(formatIssue(issue))
        }
        return documents.joinToString("\n")
    }

    private fun formatIssue(issue: Issue): String = when (issue) {
        is Epic -> EpicFormatter(epic = issue, keywordExtractor = keywordExtractor, verbosity = issueVerbosity).format(lastNumber++)
        is Story -> StoryFormatter(story = issue, keywordExtractor = keywordExtractor, verbosity = issueVerbosity).format(lastNumber++)
        is Spike -> SpikeFormatter(spike = issue, keywordExtractor = keywordExtractor, verbosity = issueVerbosity).format(lastNumber++)
    }


    override fun lastNumber(): Int = lastNumber
}