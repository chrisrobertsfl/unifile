package com.ingenifi.unifile.model.document

import com.jayway.jsonpath.JsonPath
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import org.jsoup.Jsoup
import java.util.*

class ConfluenceApi(
    private val client: HttpClient, user: String, password: String, private val apiUrlPattern: String = "https://confluence.kohls.com:8443/rest/api/content/%s?expand=body.view"
) {
    private val encodedCredentials = Base64.getEncoder().encodeToString("$user:$password".toByteArray())

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
        fun htmlToPlainText(html: String): String = HtmlConverter().convert(html)!!
    }
}