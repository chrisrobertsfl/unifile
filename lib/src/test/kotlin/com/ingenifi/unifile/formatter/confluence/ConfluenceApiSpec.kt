package com.ingenifi.unifile.formatter.confluence

import com.ingenifi.unifile.UnsecuredHttpClient
import com.ingenifi.unifile.model.document.ConfluenceApi
import com.ingenifi.unifile.model.document.FetchOption
import io.kotest.core.annotation.Ignored
import io.kotest.core.spec.style.StringSpec
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement


@Ignored
class ConfluenceApiSpec : StringSpec({
    val client = UnsecuredHttpClient.create()
    val api = ConfluenceApi(client, "TKMA5QX", "Kotlin2023!!")

    "All of it" {
        val pageid = api.fetchPageId("https://confluence.kohls.com:8443/display/LEA/Interface-EFC+Cross+Reference") ?: throw IllegalStateException("Could not find page id for given url")
        val result = api.fetch(pageid, FetchOption("title", "$.title"), FetchOption("body", "$.body.view.value",))
        val title = result.getTokens("title")
        val body = result.get("body")
        println("page id is -> $pageid")
        println("title   is -> $title")
        println("body    is -> \n$body")
    }
})

fun prettyPrintJson(jsonString: String): String {
    val jsonElement: JsonElement = Json.parseToJsonElement(jsonString)
    return Json { prettyPrint = true }.encodeToString(JsonElement.serializer(), jsonElement)
}
