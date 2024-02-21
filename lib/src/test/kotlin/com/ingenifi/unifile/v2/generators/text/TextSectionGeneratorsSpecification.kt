package com.ingenifi.unifile.v2.generators.text

import com.ingenifi.unifile.Testing.tempFile
import com.ingenifi.unifile.v2.model.*
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe


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

