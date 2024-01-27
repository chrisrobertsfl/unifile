package com.ingenifi.unifile.model.generators.jira

interface EpicChild : JiraIssue {
    val epic: Epic
}