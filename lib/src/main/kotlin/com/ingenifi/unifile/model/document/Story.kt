package com.ingenifi.unifile.model.document

data class Story(override val key: String, override val detail: String, override val title: String) : JiraIssue