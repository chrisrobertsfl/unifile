package com.ingenifi.unifile.formatter

import com.ingenifi.unifile.ConfluenceApi
import com.ingenifi.unifile.FetchOption
import com.ingenifi.unifile.FetchResult

data class ConfluenceLink(val link: String, val api: ConfluenceApi, val pageId: String, val result : FetchResult)  : Source {

    override fun description() = result.get("body") as String
    override fun title() = result.get("title") as String
    companion object {
        suspend fun create(link: String, api: ConfluenceApi): ConfluenceLink {
            val pageId = getPageId(link, api)
            val result = getResult(pageId, api)
            return ConfluenceLink(link, api, pageId, result)
        }
        private suspend fun getPageId(link: String, api: ConfluenceApi): String = api.fetchPageId(link) ?: throw IllegalArgumentException("no page id")

        private suspend fun getResult(pageId : String, api : ConfluenceApi) = api.fetch(pageId, FetchOption("title", "$.title"), FetchOption("body", "$.body.view.value", true))
    }
}