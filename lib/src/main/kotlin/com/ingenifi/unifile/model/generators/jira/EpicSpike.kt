package com.ingenifi.unifile.model.generators.jira

data class EpicSpike(override val key: String, override val detail: String, override val title: String, override val epic: Epic, override val type: String) : EpicChild