package com.ingenifi.unifile.content.conversion

import com.ingenifi.unifile.ConfluenceApi
import com.ingenifi.unifile.FetchOption
import com.ingenifi.unifile.UnsecuredHttpClient
import com.ingenifi.unifile.content.Content
import com.ingenifi.unifile.content.ContentType
import com.ingenifi.unifile.content.KeywordExtractor
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import java.io.File

data class ConfluenceLinkConverter(private val keywordExtractor: KeywordExtractor, private val username: String, private val password: String) : ContentConverter {

    private val logger by lazy { LoggerFactory.getLogger(ConfluenceLinkConverter::class.java) }
    private val client = UnsecuredHttpClient.create()
    private val api = ConfluenceApi(client, username, password)
    override fun convert(file: File): List<Content> = runBlocking {
        val links = file.readLines()
        links.mapNotNull { link ->
            try {
                val pageId = api.fetchPageId(link) ?: throw IllegalStateException("Could not find page id for given link: $link")
                val result = api.fetch(pageId, FetchOption("title", "$.title"), FetchOption("body", "$.body.view.value", true))
                val title = result.getTokens("title") ?: listOf()
                val body = result.get("body") as String
                Content(type = ContentType.CONFLUENCE_LINK, source = link, keywords = keywordExtractor.extract(body) + title, body = body)
            } catch (e: Exception) {
                null // or handle the exception as you see fit
            }
        }
    }
}