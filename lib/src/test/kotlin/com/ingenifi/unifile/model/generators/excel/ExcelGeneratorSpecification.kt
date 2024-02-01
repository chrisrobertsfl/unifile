package com.ingenifi.unifile.model.generators.excel

import com.ingenifi.unifile.Testing.resourceAsFile
import com.ingenifi.unifile.Testing.resourceAsString
import com.ingenifi.unifile.model.document.Document
import com.ingenifi.unifile.model.generators.KeywordExtractor
import com.ingenifi.unifile.model.generators.SectionGeneratorConfig
import com.ingenifi.unifile.model.generators.document.DocumentGenerator
import com.ingenifi.unifile.Verbosity
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class ExcelGeneratorSpecification : StringSpec({

    "generate" {
        val config = SectionGeneratorConfig(keywordExtractor = KeywordExtractor(), verbosity = Verbosity(false, 0))
        val sections = ExcelGenerator(config = config, number = 1, file = resourceAsFile("xlsx")).generate()
        DocumentGenerator(Document(sections = sections)).generate() shouldBe resourceAsString("expected-excel-document.txt")
    }
})

