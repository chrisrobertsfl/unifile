package com.ingenifi.unifile.formatter.jira

import com.ingenifi.unifile.formatter.Source

data class EpicSource(val epic: Epic) : Source {
    override fun description(): String = epic.description

    override fun title(): String = epic.title
}