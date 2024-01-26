package com.ingenifi.unifile.model.document

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class UnifileBodyTextSpec : StringSpec({

    "Check default content" {

        UnifileBodyText().content shouldBe """
            - Keywords
            (no keywords)
            - Summary
            (no summary)
            - Detail
            (no detail)
            
        """.trimIndent()

    }

    "Check complete content" {

        UnifileBodyText(
            headingName = Name("Epic Child Story"),
            keywords = KeywordsText.Keywords(listOf("a", "b", "c")),
            summary = SummaryText.Summary("Simple summary"),
            detail = DetailText.Detail("Simple detail")
        ).content shouldBe """
            - Epic Child Story Keywords
            a, b, c
            - Epic Child Story Summary
            Simple summary
            - Epic Child Story Detail
            Simple detail
            
        """.trimIndent()
    }
})

