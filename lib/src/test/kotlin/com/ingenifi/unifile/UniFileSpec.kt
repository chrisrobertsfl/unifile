package com.ingenifi.unifile

import io.kotest.assertions.asClue
import io.kotest.core.annotation.Ignored
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.mockk
import org.slf4j.LoggerFactory
import java.io.File

@Ignored
class UniFileSpec : StringSpec({

    val logger = LoggerFactory.getLogger(UniFile::class.java)
    "retrieve it" {
        //val file = File("/Users/TKMA5QX/projects/unifile/lib/src/test/resources/why-kotlin.pdf")
        val file = File("/Users/TKMA5QX/projects/unifile/settings.gradle.kts")
        val retrieve = UniFile(input = mockk<InputPaths>()).retrieve(file)
        logger.debug("retrieve is {}", retrieve)
        retrieve.asClue {
            it?.fileName shouldBe "settings.gradle.kts"
            it?.contentType?.startsWith("text") shouldBe true
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
        val output = OutputPath(path = null)
        UniFile(input = InputPaths(listOf("/Users/TKMA5QX/projects/unifile/settings.gradle.kts", "/Users/TKMA5QX/projects/unifile/input.txt"))).combineFiles(output)
        println("output.path = ${output.path}")
        println(File(output.path).readText())
    }
})