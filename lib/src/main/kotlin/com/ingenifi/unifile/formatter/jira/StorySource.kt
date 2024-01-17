package com.ingenifi.unifile.formatter.jira

import com.ingenifi.unifile.formatter.Source

data class StorySource(val story: Story) : Source {
    override fun description(): String = story.description

    override fun title(): String = story.title
}