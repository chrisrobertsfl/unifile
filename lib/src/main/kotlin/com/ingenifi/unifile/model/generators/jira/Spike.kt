package com.ingenifi.unifile.model.generators.jira

data class Spike(override val key: String, override val detail: String, override val title: String) : JiraIssue