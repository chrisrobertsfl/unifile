package com.ingenifi.unifile.v2.generators.text

import com.ingenifi.unifile.Testing.tempFile
import com.ingenifi.unifile.v2.model.*
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class TextSectionGeneratorsSpecification : FeatureSpec({

    fun createContentWithTags(title: String? = null, lastUpdated: String? = null, keywords: List<String>? = null, summary: String? = null, content: String? = null): String {
        val sb = StringBuilder()

        title?.let {
            sb.appendLine("<!-- Title -->")
            sb.appendLine(it)
        }

        keywords?.takeIf { it.isNotEmpty() }?.let {
            sb.appendLine("<!-- Keywords -->")
            sb.appendLine(it.joinToString(separator = ", "))
        }

        lastUpdated?.let {
            sb.appendLine("<!-- LastUpdated -->")
            sb.appendLine(it)
        }

        summary?.let {
            sb.appendLine("<!-- Summary -->")
            sb.appendLine(it)
        }

        content?.let {
            sb.appendLine("<!-- Content -->")
            sb.appendLine(it)
        }

        return sb.toString().trimEnd()
    }

    val sections = listOf(
        Section(
            id = Id(1),
            title = Title("Title is here"),
            metadata = SectionMetadata(keywords = Keywords(list = listOf("keyword")), lastUpdated = LastUpdated("20240220"), summary = Summary(text = "summary")),
            content = Content(text = "content"),
            relatedSections = RelatedSections()
        )
    )


    feature("Plain Text File") {
        scenario("Everything is provided in the file internally") {
            val content = createContentWithTags(title = "Title is here", keywords = listOf("keyword"), lastUpdated = "20240220", summary = "summary", content = "content")
            val file = tempFile(content = content)
            TextSectionGenerator(number = 1, file = file).generate() shouldBe sections
        }
        scenario("Last updated is embedded in file name like 20240220-Title-is-here.txt") {
            val content = createContentWithTags(title = "Title is here", keywords = listOf("keyword"), summary = "summary", content = "content")
            val file = tempFile(name = "20240220-Title-is-here", content = content)
            TextSectionGenerator(number = 1, file = file).generate() shouldBe sections
        }
        scenario("Last updated comes from when file was created") {
            val content = createContentWithTags(title = "Title is here", keywords = listOf("keyword"), summary = "summary", content = "content")
            val file = tempFile(name = "Title-is-here", content = content)
            TextSectionGenerator(number = 1, file = file).generate()[0].metadata.lastUpdated.text shouldBe LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))
        }
        scenario("Pull title only out from file") {
            val content = createContentWithTags(keywords = listOf("keyword"), lastUpdated = "20240220", summary = "summary", content = "content")
            val file = tempFile(name = "Title-is-here", content = content)
            println("file.name = ${file.name}")
            TextSectionGenerator(number = 1, file = file).generate() shouldBe sections
        }
    }
})

