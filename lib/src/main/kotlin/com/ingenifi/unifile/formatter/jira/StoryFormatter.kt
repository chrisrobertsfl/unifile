package com.ingenifi.unifile.formatter.jira

import com.ingenifi.unifile.formatter.Delegate
import com.ingenifi.unifile.formatter.DocumentFormatter
import com.ingenifi.unifile.formatter.KeywordExtractor

data class StoryFormatter(private val story: Story, val keywordExtractor: KeywordExtractor) : DocumentFormatter {
    private val delegate = Delegate(StorySource(story), keywordExtractor)

    private var lastNumber = 0

    override fun format(number: Int): String {
        lastNumber = number
        val keywords = mutableListOf<String>()
        keywords.add("story")
        keywords.add(story.key)
        return delegate.format(number, templatePath = "story-document.tmpl", replacements = mapOf<String, String>("key" to story.key, "title" to story.title), additionalKeywords = keywords)
    }

    override fun lastNumber(): Int = lastNumber + 1
}