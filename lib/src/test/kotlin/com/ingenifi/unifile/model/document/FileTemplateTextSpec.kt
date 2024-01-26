package com.ingenifi.unifile.model.document

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import java.io.File

class FileTemplateTextSpec : StringSpec({
    "read file and substitute values" {
        FileTemplateText(
            file = File("src/main/resources/templates/plain.tmpl"),
            "keywords" to listOf("Hello", "World").joinToString(", "), "description" to "This is my description").content shouldBe """
            - Introduction:
            keywords are: Hello, World

            - Description
            This is my description
        """.trimIndent()
    }
})
