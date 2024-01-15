package com.ingenifi.unifile

import com.ingenifi.unifile.content.conversion.ConfluenceLinkConverter
import com.jayway.jsonpath.JsonPath
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import org.jsoup.Jsoup
import org.slf4j.LoggerFactory
import java.util.*

class ConfluenceApi(
    private val client: HttpClient, user: String, password: String, private val apiUrlPattern: String = "https://confluence.kohls.com:8443/rest/api/content/%s?expand=body.view"
) {
    private val encodedCredentials = Base64.getEncoder().encodeToString("$user:$password".toByteArray())

    private val logger by lazy { LoggerFactory.getLogger(ConfluenceLinkConverter::class.java) }

    suspend fun fetchPageId(url: String): String? {
        val doc = Jsoup.parse(httpGet(url))
        val pageIdMetaTag = doc.select("meta[name=ajs-page-id]").firstOrNull()
        return pageIdMetaTag?.attr("content")
    }

    suspend fun fetch(pageId: String, vararg options: FetchOption) = fetch(pageId, options.toList())
    suspend fun fetch(pageId: String, options: List<FetchOption>): FetchResult {
        val apiUrl = apiUrlPattern.format(pageId)
        val response = httpGet(apiUrl)
        val resultMap = mutableMapOf<String, Any>()
        options.forEach { option ->
            val result = JsonPath.read<Any?>(response, option.path)
            resultMap[option.key] = if (option.interpretAsHtml) {
                htmlToPlainText(result.toString())
            } else {
                result
            }
        }
        return FetchResult(resultMap)
    }

    private suspend fun httpGet(request: String): String = client.get(request) {
        header("Authorization", "Basic $encodedCredentials")
    }.bodyAsText()

    companion object {
        fun htmlToPlainText(html: String): String = Jsoup.parse(html).text()
    }
}