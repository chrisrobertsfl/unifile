package com.ingenifi.unifile.v2.generators.text

import com.ingenifi.unifile.Testing.tempFile
import com.ingenifi.unifile.v2.model.*
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe


class TextSectionGeneratorsSpecification : FeatureSpec({
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
            val content = """
                <!-- Title -->
                Title is here
                <!-- Keywords -->
                keyword
                <!-- LastUpdated -->
                20240220
                <!-- Summary -->
                summary
                <!-- Content -->
                content
            """.trimIndent()
            val file = tempFile(content = content)
            TextSectionGenerator(number = 1, file = file).generate() shouldBe sections
        }
        scenario("Last updated is embedded in file name like 20240220-Title-is-here.txt") {
            val content = """
                <!-- Title -->
                Title is here
                <!-- Keywords -->
                keyword
                <!-- Summary -->
                summary
                <!-- Content -->
                content
            """.trimIndent()
            val file = tempFile(name = "20240220-Title-is-here", content = content)
            println("file.name = ${file.name}")
            TextSectionGenerator(number = 1, file = file).generate() shouldBe sections
        }
    }

})

