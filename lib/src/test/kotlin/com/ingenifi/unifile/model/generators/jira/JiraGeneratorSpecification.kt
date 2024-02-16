package com.ingenifi.unifile.model.generators.jira

import com.ingenifi.unifile.ParameterStore
import com.ingenifi.unifile.Testing.output
import com.ingenifi.unifile.Testing.resourceAsFile
import com.ingenifi.unifile.Testing.resourceAsString
import com.ingenifi.unifile.model.document.Document
import com.ingenifi.unifile.model.document.Section
import com.ingenifi.unifile.model.generators.KeywordExtractor
import com.ingenifi.unifile.model.generators.SectionGeneratorConfig
import com.ingenifi.unifile.model.generators.document.DocumentGenerator
import com.ingenifi.unifile.Verbosity
import com.ingenifi.unifile.model.document.Sections
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class JiraGeneratorSpecification : StringSpec({
    val config = SectionGeneratorConfig(keywordExtractor = KeywordExtractor(), verbosity = Verbosity(verbose = true, level = 0), parameterStore = ParameterStore.loadProperties())
    fun generate(jiraType: String) = JiraGenerator(config = config, number = 1, file = resourceAsFile(name = jiraType, extension = "jira")).generate()
    fun validate(jiraType: String, sections: Sections, output: Boolean = false) {
        val actual = DocumentGenerator(Document(sections = sections.list)).generate()
        val expected = resourceAsString("expected-${jiraType}-document.txt")
        if (output) {
            actual.output("actual")
            expected.output("expected")
        }
        actual shouldBe expected
    }

    "generate story" {
        validate("story", generate("story"))
    }

    "generate spike" {
        validate("spike", generate("spike"))
    }

    "generate bug" {
        validate("bug", generate("bug"))
    }

    "generate epic".config(enabled = false) {
        validate("epic", generate("epic"), output = true)
    }
})


