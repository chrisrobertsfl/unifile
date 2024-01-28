package com.ingenifi.unifile.model.generators.confluence

import com.ingenifi.unifile.model.document.*
import com.ingenifi.unifile.model.generators.SectionGenerator
import com.ingenifi.unifile.model.generators.SectionGeneratorConfig
import com.ingenifi.unifile.verbosity.VerbosePrinter
import com.ingenifi.unifile.verbosity.VerbosePrinting
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import java.io.File

data class ConfluenceGenerator(val config: SectionGeneratorConfig, val number: Int, val file: File, val headingNameString: String = "Confluence Page") : SectionGenerator,
    VerbosePrinting by VerbosePrinter(config.verbosity) {
    private val api: ConfluenceApi = ConfluenceApi(config.client, config.parameterStore.getParameter("username"), config.parameterStore.getParameter("password"))
    private val headingName = Name(headingNameString)
    override fun generate(): List<Section> = runBlocking {
        verbosePrint("Processing Confluence file '${file.name}'")
        val withLevel = config.verbosity.increasedLevel(1)
        file.readLines().map {
            async { createLink(it) }
        }.map {
            createSection(it.await(), withLevel)
        }
    }

    private fun createSection(link: ConfluenceLink, withLevel : Int): Section {
        verbosePrint("Processing Confluence link '${link.url}", withLevel = withLevel)
        return Section(
            heading = Heading(
                headingName = headingName, sectionNumber = SectionNumber(listOf(Level(number))), title = Title(link.title)
            ), text = UnifileBodyText(headingName = headingName, keywords = KeywordsText.Keywords(config.keywordExtractor.extract(link.detail)), detail = DetailText.Detail(link.detail))
        )
    }

    private suspend fun createLink(url: String): ConfluenceLink {
        val pageId = getPageId(url)
        val result = getResult(pageId)
        return ConfluenceLink(url = url, pageId = pageId, title = result.getAsString("title"), detail = result.getAsString("body", trim = true))
    }

    private suspend fun getPageId(url: String): String = api.fetchPageId(url) ?: throw IllegalArgumentException("no page id")
    private suspend fun getResult(pageId: String) = api.fetch(pageId, FetchOption("title", "$.title"), FetchOption("body", "$.body.view.value", true))

}