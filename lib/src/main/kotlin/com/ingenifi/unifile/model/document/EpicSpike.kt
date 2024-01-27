package com.ingenifi.unifile.model.document

data class EpicSpike(override val key: String, override val detail: String, override val title: String, override val epic: Epic) : EpicChild