package com.ingenifi.unifile.formatter.jira

import com.ingenifi.unifile.formatter.Source

data class SpikeSource(val spike: Spike) : Source {
    override fun description(): String = spike.description

    override fun title(): String = spike.title
}