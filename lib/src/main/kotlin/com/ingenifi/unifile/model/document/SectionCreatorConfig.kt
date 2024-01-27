package com.ingenifi.unifile.model.document

import com.ingenifi.unifile.KeywordExtractor
import com.ingenifi.unifile.Verbosity
import com.ingenifi.unifile.formatter.jira.JiraApi

data class SectionCreatorConfig(
    val jiraIssue: JiraIssue, val keywordExtractor: KeywordExtractor, val api: JiraApi, val number: SectionNumber, val issueCreator: IssueCreator, val verbosity: Verbosity
)