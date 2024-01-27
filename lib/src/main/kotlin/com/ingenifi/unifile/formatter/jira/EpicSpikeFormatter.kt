package com.ingenifi.unifile.formatter.jira

import com.ingenifi.unifile.Verbosity
import com.ingenifi.unifile.formatter.DocumentFormatter
import com.ingenifi.unifile.KeywordExtractor
import com.ingenifi.unifile.formatter.toc.TableOfContents

data class EpicSpikeFormatter(
    private val spike: Spike, private val epic: Epic, private val childNumber: Int, val keywordExtractor: KeywordExtractor, val toc: TableOfContents, private val verbosity: Verbosity
) : DocumentFormatter by EpicChildFormatter(
    issue = spike,
    epic = epic,
    childNumber = childNumber,
    keywordExtractor = keywordExtractor,
    toc = toc,
    templatePath = "epic-spike-document.tmpl",
    additionalKeywords = listOf("spike") + epic.additionalKeywords,
    verbosity = verbosity
)
