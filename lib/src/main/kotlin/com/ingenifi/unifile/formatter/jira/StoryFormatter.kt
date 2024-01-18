package com.ingenifi.unifile.formatter.jira

import com.ingenifi.unifile.VerbosePrinter
import com.ingenifi.unifile.VerbosePrinting
import com.ingenifi.unifile.Verbosity
import com.ingenifi.unifile.formatter.Delegate
import com.ingenifi.unifile.formatter.DocumentFormatter
import com.ingenifi.unifile.formatter.KeywordExtractor

data class StoryFormatter(private val story: Story, val keywordExtractor: KeywordExtractor, private val verbosity: Verbosity) : DocumentFormatter, VerbosePrinting by VerbosePrinter(verbosity) {

    private val delegate = Delegate(StorySource(story), keywordExtractor)

    private var lastNumber = 0

    override fun format(number: Int): String {
        verbosePrint("Processing story ${story.key}: '${story.title}'")
        lastNumber = number
        val keywords = mutableListOf<String>()
        keywords.add("story")
        keywords.add(story.key)
        return delegate.format(number, templatePath = "story-document.tmpl", replacements = mapOf<String, String>("key" to story.key, "title" to story.title), additionalKeywords = keywords)
    }

    override fun lastNumber(): Int = lastNumber + 1
}