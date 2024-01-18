package com.ingenifi.unifile.formatter.jira

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

class JiraApi(private val client: HttpClient, private val jiraBaseUrl: String, private val apiToken: String) {
    private val mapper = jacksonObjectMapper().apply {
        enable(SerializationFeature.INDENT_OUTPUT)
    }


    fun getChildren(epicKey: String): List<String> = runBlocking {
        val authHeader = "Bearer $apiToken"
        val jqlQuery = URLEncoder.encode("\"Epic Link\" = $epicKey", StandardCharsets.UTF_8.toString())
        val response = client.get("$jiraBaseUrl/rest/api/2/search?jql=$jqlQuery") {
            header("Authorization", authHeader)
            header("Accept", "application/json")
        }.bodyAsText()

        val jsonElement = Json.parseToJsonElement(response)
        val issues = jsonElement.jsonObject["issues"]?.jsonArray ?: return@runBlocking emptyList()

        issues.mapNotNull { issue ->
            issue.jsonObject["key"]?.jsonPrimitive?.content
        }
    }

    fun getIssue(key: String): Map<String, Any>? = runBlocking {
        val authHeader = "Bearer $apiToken"
        val urlString = "$jiraBaseUrl/rest/api/2/issue/$key"
        client.get(urlString) {
            header("Authorization", authHeader)
            header("Accept", "application/json")
        }.body<HttpResponse>().let {
            val epicData = mapper.readValue<Map<String, Any>>(it.bodyAsText())
            epicData["fields"] as? Map<String, Any>
        }
    }
}
