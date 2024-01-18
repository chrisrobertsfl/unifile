package com.ingenifi.unifile.formatter.jira

import com.ingenifi.unifile.VerbosePrinter
import com.ingenifi.unifile.VerbosePrinting
import com.ingenifi.unifile.Verbosity
import com.ingenifi.unifile.formatter.Delegate
import com.ingenifi.unifile.formatter.DocumentFormatter
import com.ingenifi.unifile.formatter.KeywordExtractor

data class EpicStoryFormatter(private val story: Story, private val epic: Epic, private val childNumber: Int, val keywordExtractor: KeywordExtractor, private val verbosity: Verbosity) :
    DocumentFormatter, VerbosePrinting by VerbosePrinter(verbosity) {

    private val delegate = Delegate(StorySource(story), keywordExtractor)

    private var lastNumber = 0
    override fun format(number: Int): String {
        verbosePrint("Processing child story ${story.key}: '${story.title}'")
        lastNumber = number
        val keywords = mutableListOf<String>()
        keywords.add("child")
        keywords.add("story")
        keywords.add(story.key)
        keywords.add(epic.title)
        return delegate.format(
            number, templatePath = "epic-story-document.tmpl", replacements = mapOf<String, String>(
                "epicKey" to epic.key, "epicTitle" to epic.title, "key" to story.key, "title" to story.title, "epicNumber" to number.toString(), "number" to childNumber.toString()
            ), additionalKeywords = keywords
        )

    }

    override fun lastNumber(): Int = lastNumber + 1
}