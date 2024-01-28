package com.ingenifi.unifile.model.generators.jira

data class Bug(override val key: String, override val detail: String, override val title: String, override val type: String) : JiraIssue