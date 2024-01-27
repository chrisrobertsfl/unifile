package com.ingenifi.unifile.formatter.jira

import com.ingenifi.unifile.Verbosity
import com.ingenifi.unifile.formatter.DocumentFormatter
import com.ingenifi.unifile.KeywordExtractor
import com.ingenifi.unifile.formatter.toc.TableOfContents

data class EpicStoryFormatter(
    private val story: Story, private val epic: Epic, private val childNumber: Int, val keywordExtractor: KeywordExtractor, val toc: TableOfContents, private val verbosity: Verbosity
) : DocumentFormatter by EpicChildFormatter(
    issue = story,
    epic = epic,
    childNumber = childNumber,
    keywordExtractor = keywordExtractor,
    toc = toc,
    templatePath = "epic-story-document.tmpl",
    additionalKeywords = listOf("story") + epic.additionalKeywords,
    verbosity = verbosity
)