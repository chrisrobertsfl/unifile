package com.ingenifi.unifile.model.document

data class Epic(override val key: String, override val detail: String, val summary: String, override val title: String, val children: List<JiraIssue> = listOf()) : JiraIssue