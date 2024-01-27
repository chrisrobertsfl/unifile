package com.ingenifi.unifile.model.document

import kotlinx.coroutines.runBlocking
import java.io.File
import com.ingenifi.unifile.model.document.SectionGenerator.Config

data class ConfluenceGenerator(val config: Config, val number: Int, val file: File, val headingNameString: String = "Confluence Page") : SectionGenerator {
    private val api: ConfluenceApi = ConfluenceApi(config.client, config.parameterStore.getParameter("username"), config.parameterStore.getParameter("password"))
    override fun generate(): List<Section> = runBlocking {
        file.readLines().map { createLink(it) }.map {
            createSection(it)
        }
    }

    private fun createSection(link: ConfluenceLink) = Section(
        heading = Heading(
            headingName = Name("Confluence Page"), sectionNumber = SectionNumber(listOf(Level(number))), title = Title(link.title)
        ), text = UnifileBodyText(headingName = Name("Confluence Page"), keywords = KeywordsText.Keywords(config.keywordExtractor.extract(link.detail)), detail = DetailText.Detail(link.detail))
    )

    private suspend fun createLink(url: String): ConfluenceLink {
        val pageId = getPageId(url)
        val result = getResult(pageId)
        return ConfluenceLink(url = url, pageId = pageId, title = result.getAsString("title"), detail = result.getAsString("body", trim = true))
    }

    private suspend fun getPageId(url: String): String = api.fetchPageId(url) ?: throw IllegalArgumentException("no page id")
    private suspend fun getResult(pageId: String) = api.fetch(pageId, FetchOption("title", "$.title"), FetchOption("body", "$.body.view.value", true))

}