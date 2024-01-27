package com.ingenifi.unifile.model.generators.jira

data class EpicStory(override val key: String, override val detail: String, override val title: String, override val epic: Epic) : EpicChild