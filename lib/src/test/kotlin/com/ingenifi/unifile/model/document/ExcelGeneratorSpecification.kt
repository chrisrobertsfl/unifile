package com.ingenifi.unifile.model.document

import com.ingenifi.unifile.KeywordExtractor
import com.ingenifi.unifile.Verbosity
import com.ingenifi.unifile.model.document.SectionGenerator.Config
import com.ingenifi.unifile.resourceAsString
import com.ingenifi.unifile.simpleFile
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.mockk

class ExcelGeneratorSpecification : StringSpec({

    "generate" {
        val config = Config(keywordExtractor = KeywordExtractor(), verbosity = mockk<Verbosity>())
        val sections = ExcelGenerator(config = config, number = 1, file = simpleFile("xlsx")).generate()
        DocumentGenerator(Document(sections = sections)).generate() shouldBe resourceAsString("expected-excel-document.txt")
    }
})

