package com.ingenifi.unifile.model.document

data class Bug(override val key: String, override val detail: String, override val title: String) : JiraIssue