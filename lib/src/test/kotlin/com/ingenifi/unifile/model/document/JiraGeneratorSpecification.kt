package com.ingenifi.unifile.model.document

import com.ingenifi.unifile.*
import com.ingenifi.unifile.formatter.jira.JiraApi
import com.ingenifi.unifile.model.generators.SectionGenerator
import com.ingenifi.unifile.model.generators.SectionGeneratorConfig
import com.ingenifi.unifile.model.generators.document.DocumentGenerator
import com.ingenifi.unifile.model.generators.jira.IssueCreator
import com.ingenifi.unifile.model.generators.jira.SectionCreator
import com.ingenifi.unifile.model.generators.jira.SectionCreatorConfig
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.runBlocking
import java.io.File

class JiraGeneratorSpecification : StringSpec({
    val config = SectionGeneratorConfig(keywordExtractor = KeywordExtractor(), verbosity = Verbosity(verbose = true, level = 0), parameterStore = ParameterStore.loadProperties())
    fun generate(jiraType: String) = JiraGenerator(config = config, number = 1, file = resourceAsFile(name = jiraType, extension = "jira")).generate()
    fun validate(jiraType: String, sections: List<Section>, output: Boolean = false) {
        val actual = DocumentGenerator(Document(sections = sections)).generate()
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


data class JiraGenerator(val config: SectionGeneratorConfig, val number: Int, val file: File) : SectionGenerator {
    private val api = JiraApi(config.client, config.parameterStore.getParameter("jiraBaseUrl"), config.parameterStore.getParameter("apiToken"))
    private val issueCreator = IssueCreator(api)
    override fun generate(): List<Section> = runBlocking {

        file.readLines().map { issueCreator.create(it) }.map {
            val sectionCreatorConfig = SectionCreatorConfig(
                jiraIssue = it, keywordExtractor = config.keywordExtractor, api = api, issueCreator = issueCreator, number = SectionNumber(number), verbosity = config.verbosity
            )
            SectionCreator.create(sectionCreatorConfig)
        }.flatten()
    }


}

