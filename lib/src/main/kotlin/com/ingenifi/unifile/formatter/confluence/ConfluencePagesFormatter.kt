package com.ingenifi.unifile.formatter.confluence

import com.ingenifi.unifile.ParameterStore
import com.ingenifi.unifile.VerbosePrinter
import com.ingenifi.unifile.VerbosePrinting
import com.ingenifi.unifile.Verbosity
import com.ingenifi.unifile.formatter.DocumentFormatter
import com.ingenifi.unifile.KeywordExtractor
import com.ingenifi.unifile.formatter.toc.TableOfContents
import com.ingenifi.unifile.model.document.ConfluenceApi
import io.ktor.client.*
import kotlinx.coroutines.runBlocking
import java.io.File

data class ConfluencePagesFormatter(
    val parameterStore: ParameterStore,
    private val client: HttpClient,
    private val file: File,
    private val keywordExtractor: KeywordExtractor,
    private val verbosity: Verbosity,
    val toc: TableOfContents
) : DocumentFormatter, VerbosePrinting by VerbosePrinter(verbosity) {
    private val username = parameterStore.getParameter("username")
    private val password = parameterStore.getParameter("password")
    private val api = ConfluenceApi(client, username, password)
    private var lastNumber = 0

    override fun format(number: Int): String = runBlocking { formatSuspended(number) }
    private suspend fun formatSuspended(number: Int): String {
        lastNumber = number
        return file.readLines().map { link -> processLink(link) }.joinToString("\n")
    }

    private suspend fun processLink(link: String): String {
        val confluenceLink = createConfluenceLink(link)
        verbosePrint("Processing link: ${confluenceLink.link}")
        return ConfluencePageFormatter(confluenceLink, keywordExtractor, toc).format(lastNumber++)
    }

    private suspend fun createConfluenceLink(link: String): ConfluenceLink = ConfluenceLink.create(link = link, api = api)
    override fun lastNumber(): Int = lastNumber
}