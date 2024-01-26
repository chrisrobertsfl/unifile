package com.ingenifi.unifile.formatter.jira

import com.ingenifi.unifile.Verbosity
import com.ingenifi.unifile.formatter.DocumentFormatter
import com.ingenifi.unifile.formatter.KeywordExtractor
import com.ingenifi.unifile.formatter.toc.TableOfContents

data class EpicBugFormatter(
    private val bug: Bug, private val epic: Epic, private val childNumber: Int, val keywordExtractor: KeywordExtractor, val toc: TableOfContents, private val verbosity: Verbosity
) : DocumentFormatter by EpicChildFormatter(
    issue = bug,
    epic = epic,
    childNumber = childNumber,
    keywordExtractor = keywordExtractor,
    toc = toc,
    templatePath = "epic-bug-document.tmpl",
    additionalKeywords = listOf("big") + epic.additionalKeywords,
    verbosity = verbosity
)
