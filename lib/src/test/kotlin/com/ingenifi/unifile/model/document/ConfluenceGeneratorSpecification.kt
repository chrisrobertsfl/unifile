package com.ingenifi.unifile.model.document

import com.ingenifi.unifile.ParameterStore
import com.ingenifi.unifile.Verbosity
import com.ingenifi.unifile.KeywordExtractor
import com.ingenifi.unifile.model.generators.SectionGeneratorConfig
import com.ingenifi.unifile.model.generators.confluence.ConfluenceGenerator
import com.ingenifi.unifile.resourceAsFile
import com.ingenifi.unifile.vpnOn
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.mockk


class ConfluenceGeneratorSpecification : StringSpec({

    "generate".config(enabledIf = { vpnOn() }) {
        val config = SectionGeneratorConfig(keywordExtractor = KeywordExtractor(), verbosity = mockk<Verbosity>(), parameterStore = ParameterStore.loadProperties())
        ConfluenceGenerator(config = config, number = 1, file = resourceAsFile("confluence")).generate() shouldBe listOf(
            Section(
                heading = Heading(
                    headingName = Name("Confluence Page"), sectionNumber = SectionNumber(listOf(Level(1))), title = Title("Hello Confluence")
                ), text = UnifileBodyText(headingName = Name("Confluence Page"), keywords = KeywordsText.Keywords(), detail = DetailText.Detail("Simple detail"))
            )
        )
    }
})


