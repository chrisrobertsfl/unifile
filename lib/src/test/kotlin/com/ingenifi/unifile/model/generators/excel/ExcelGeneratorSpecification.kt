package com.ingenifi.unifile.model.generators.excel

import com.ingenifi.unifile.model.document.Document
import com.ingenifi.unifile.model.generators.KeywordExtractor
import com.ingenifi.unifile.verbosity.Verbosity
import com.ingenifi.unifile.model.generators.SectionGeneratorConfig
import com.ingenifi.unifile.model.generators.document.DocumentGenerator
import com.ingenifi.unifile.resourceAsString
import com.ingenifi.unifile.resourceAsFile
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.mockk

class ExcelGeneratorSpecification : StringSpec({

    "generate" {
        val config = SectionGeneratorConfig(keywordExtractor = KeywordExtractor(), verbosity = mockk<Verbosity>())
        val sections = ExcelGenerator(config = config, number = 1, file = resourceAsFile("xlsx")).generate()
        DocumentGenerator(Document(sections = sections)).generate() shouldBe resourceAsString("expected-excel-document.txt")
    }
})

