package com.ingenifi.unifile.formatter.jira

import com.ingenifi.unifile.formatter.DocumentFormatter
import com.ingenifi.unifile.formatter.KeywordExtractor
import io.ktor.client.*
import java.io.File

data class JiraFormatter(val file: File, val client: HttpClient, val keywordExtractor: KeywordExtractor) : DocumentFormatter {
    private val api = JiraApi(client, "https://jiradc.kohls.com:8443", "Mzg5OTE4MDk2Njg2OuVpAR0/UMhgdWrE/8VhjHIirbIu")
    private var lastNumber = 0
    private val factory = IssueFactory(api, true)

    override fun format(number: Int): String {
        val issues = file.readLines().map(factory::create)
        val issuesFormatter = IssuesFormatter(issues, keywordExtractor)
        val text = issuesFormatter.format(number)
        lastNumber = issuesFormatter.lastNumber()
        return text
    }

    override fun lastNumber(): Int = lastNumber
}