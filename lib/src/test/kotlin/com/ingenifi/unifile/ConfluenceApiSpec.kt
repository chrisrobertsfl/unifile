package com.ingenifi.unifile

import io.kotest.core.annotation.Ignored
import io.kotest.core.spec.style.StringSpec


@Ignored
class ConfluenceApiSpec : StringSpec({
    val client = UnsecuredHttpClient.create()
    val api = ConfluenceApi(client, "TKMA5QX", "Kotlin2023!!")

    "All of it" {
        val pageid = api.fetchPageId("https://confluence.kohls.com:8443/display/OE/Standard+and+Design+Practices") ?: throw IllegalStateException("Could not find page id for given url")
        val result = api.fetch(pageid, FetchOption("title", "$.title"), FetchOption("body", "$.body.view.value", true))
        val title = result.getTokens("title")
        val body = result.get("body")
        println("page id is -> $pageid")
        println("title   is -> $title")
        println("body    is -> $body")
    }
})


