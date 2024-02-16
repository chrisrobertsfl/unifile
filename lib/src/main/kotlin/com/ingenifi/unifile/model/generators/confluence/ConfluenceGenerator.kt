package com.ingenifi.unifile.model.generators.confluence

import com.ingenifi.unifile.VerbosePrinter
import com.ingenifi.unifile.VerbosePrinting
import com.ingenifi.unifile.model.document.*
import com.ingenifi.unifile.model.generators.SectionsGenerator
import com.ingenifi.unifile.model.generators.SectionGeneratorConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import java.io.File

data class ConfluenceGenerator(val config: SectionGeneratorConfig, val number: Int, val file: File, val headingName: HeadingName = HEADING_NAME) : SectionsGenerator,
    VerbosePrinting by VerbosePrinter(config.verbosity) {
    private val api: ConfluenceApi = ConfluenceApi(config.client, config.parameterStore.getParameter("username"), config.parameterStore.getParameter("password"))
    private var numberProcessed = 1
    override fun generate(): Sections = runBlocking {
        verbosePrint("Processing Confluence file '${file.name}'")
        val withLevel = config.verbosity.increasedLevel(1)
        val links = file.readLines().map { createLink(it) }

        val sections = links.mapIndexed { index, link ->
            async(Dispatchers.Default) {
                createSection(link, withLevel, index + 1) // Pass index + 1 to maintain 1-based numbering
            }
        }.awaitAll()

        Sections(list = sections, numberProcessed = links.size) // Update numberProcessed based on the size of links
    }

    private fun createSection(link: ConfluenceLink, withLevel: Int, index: Int): Section {
        verbosePrint("Processing Confluence link '${link.url}", withLevel = withLevel)
        val sectionNumber = SectionNumber(listOf(Level(number + index))) // Use index for unique section numbering
        val section = Section(
            heading = Heading(
                headingName = headingName, sectionNumber = sectionNumber, title = TitleText.Title(link.title)
            ), bodyText = UnifileBodyText(
                headingName = headingName,
                keywords = KeywordsText.Keywords(config.keywordExtractor.extract(link.detail)),
                detail = DetailText.Detail(link.detail)
            )
        )
        return section
    }

    private fun createLink(url: String): ConfluenceLink  {
        val pageId = runBlocking { getPageId(url) }
        val result = runBlocking { getResult(pageId) }
        return ConfluenceLink(url = url, pageId = pageId, title = result.getAsString("title"), detail = result.getAsString("body", trim = true))
    }

    private suspend fun getPageId(url: String): String = api.fetchPageId(url) ?: throw IllegalArgumentException("no page id")
    private suspend fun getResult(pageId: String) = api.fetch(pageId, FetchOption("title", "$.title"), FetchOption("body", "$.body.view.value", true))

    companion object {
        val HEADING_NAME = Name("Confluence Page")
    }
}