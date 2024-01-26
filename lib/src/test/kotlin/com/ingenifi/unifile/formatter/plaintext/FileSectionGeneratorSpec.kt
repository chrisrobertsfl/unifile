package com.ingenifi.unifile.formatter.plaintext

import com.ingenifi.unifile.model.document.*
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import java.io.File

class FileSectionGeneratorSpec : StringSpec({

    "format it from file" {
        val file = File("src/test/resources/simple.txt")
        val templateResource = "templates/plain.tmpl"
        val formatter = FileSectionGenerator(
            file = file,
            templateResource = templateResource,
            headingName = "Document",
            number = 1,
            substitutions = mapOf("keywords" to listOf("Hello", "World").joinToString(", "), "description" to file.readText())
        )

        val sections = formatter.sections()
        sections shouldBe listOf(
            Section(
                heading = Heading(
                    headingName = Name("Document"), sectionNumber = SectionNumber(listOf(Level(1))), title = Title("simple.txt")
                ), text = ResourceTemplateText(
                    resource = templateResource, substitutions = mapOf(
                        "keywords" to listOf("Hello", "World").joinToString(", "), "description" to "Hello Plain Text"
                    )
                )
            )
        )
        val tableOfContents = TableOfContents(headings = sections.map { it.heading })
        val body = Body(sections = sections)
        val document = Document(tableOfContents = tableOfContents, body = body)
        DocumentGenerator(document = document, justifySectionNumbers = true).generate() shouldBe """
            ========================
            Table Of Contents
            ========================
            1. Document - simple.txt


            ========================
            1. Document - simple.txt
            ========================
            - Introduction:
            keywords are: Hello, World

            - Description
            Hello Plain Text


        """.trimIndent()

    }
})

data class FileSectionGenerator(
    val file: File,
    val templateResource: String,
    val substitutions: Map<String, String> = mapOf(),
    val bodyText: BodyText = ResourceTemplateText(templateResource, substitutions),
    val headingName: String,
    val number: Int
) {
    constructor(file: File, templateResource: String, headingName: String, number: Int, vararg substitutions: Pair<String, String>) : this(
        file = file,
        templateResource,
        headingName = headingName,
        number = number,
        substitutions = mapOf(*substitutions)
    )

    fun sections(): List<Section> {
        return listOf(
            Section(
                heading = Heading(
                    headingName = Name(headingName), sectionNumber = SectionNumber(listOf(Level(1))), title = Title(file.name)
                ), text = bodyText
            )
        )
    }
}