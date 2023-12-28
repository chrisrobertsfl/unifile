package com.ingenifi.unifile

import io.kotest.assertions.asClue
import io.kotest.core.annotation.Ignored
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.mockk
import java.io.File

class UniFileSpec : StringSpec({

    "retrieve it" {
        val file = File("/Users/TKMA5QX/projects/unifile/lib/src/test/resources/why-kotlin.pdf")
        val retrieve = UniFile(input = mockk<InputPaths>()).retrieve(file)
        retrieve.asClue {
            it?.fileName shouldBe "why-kotlin.pdf"
            it?.contentType shouldBe "application/pdf"
            it?.contents?.isNotEmpty() shouldBe true
        }
    }

    "format it" {
        UniFile(input = mockk<InputPaths>()).format(UniFileEntry(fileName = "fileName", contentType = "contentType", contents = "contents")) shouldBe """
            File Name: fileName
            Content Type: contentType
            Contents:
            contents
            ---
            
        """.trimIndent()
    }

    "do all" {
        val output = OutputPath("/tmp/output")
        UniFile(input = InputPaths(listOf("/Users/TKMA5QX/projects/unifile"))).combineFiles(output)
        println("output.path = ${output.path}")

    }
})