package com.ingenifi.unifile.v2.generators

import com.ingenifi.unifile.Testing.tempFile
import com.ingenifi.unifile.model.generators.KeywordExtractor
import com.ingenifi.unifile.v2.model.*
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import kotlin.text.RegexOption.DOT_MATCHES_ALL
import java.io.File


class TextSectionGeneratorsSpecification : FeatureSpec({
    val sections = listOf(
        Section(
            id = Id(1),
            title = Title("title"),
            metadata = SectionMetadata(keywords = Keywords(list = listOf("keyword")), lastUpdated = LastUpdated("2024-02-20"), summary = Summary(text = "summary")),
            content = Content(text = "content"),
            relatedSections = RelatedSections()
        )
    )


    feature("Plain Text File") {

        scenario("Everything is provided in the file internally") {
            val content = """
                <!-- Title -->
                title
                <!-- Keywords -->
                keyword
                <!-- LastUpdated -->
                2024-02-20
                <!-- Summary -->
                summary
                <!-- Content -->
                content
            """.trimIndent()
            val file = tempFile(content = content)
            TextSectionGenerator(number = 1, file = file).generate() shouldBe sections
        }
    }
})

data class TextSectionGenerator(val number: Int, val file: File, val keywordExtractor: KeywordExtractor = KeywordExtractor()) {
    fun generate(): List<Section> {
        val fileData = FileData.from(file)
        val id = Id(number = number)
        val metadata = SectionMetadata(summary = fileData.summary, keywords = fileData.keywords, lastUpdated = fileData.lastUpdated)
        val section = Section(id = id, title = fileData.title, metadata = metadata, content = fileData.content)
        return listOf(section)
    }
}

data class FileSectionExtractor(private val fileContent: String) {
    private fun extract(tag: String): String = "<!-- $tag -->(.*?)(?=(<!--)|\$)".toRegex(DOT_MATCHES_ALL).find(fileContent)?.groups?.get(1)?.value?.trim() ?: ""
    fun title() = Title(extract("Title"))
    fun summary() = Summary(text = extract("Summary"))
    fun content() = Content(text = extract("Content"))
    fun lastUpdated() = LastUpdated(text = extract("LastUpdated"))
    fun keywords() = Keywords(list = extract("Keywords").split(",").map { it.trim() })
}

data class FileData(val title: Title, val summary: Summary, val content: Content, val lastUpdated: LastUpdated, val keywords: Keywords) {
    companion object {
        fun from(file: File): FileData {
            val extractor = FileSectionExtractor(file.readText())
            return FileData(title = extractor.title(), summary = extractor.summary(), content = extractor.content(), lastUpdated = extractor.lastUpdated(), keywords = extractor.keywords())
        }
    }
}
