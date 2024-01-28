package com.ingenifi.unifile.model.generators.jira

import com.ingenifi.unifile.model.document.Section
import com.ingenifi.unifile.model.document.SectionNumber
import com.ingenifi.unifile.model.generators.SectionGenerator
import com.ingenifi.unifile.model.generators.SectionGeneratorConfig
import kotlinx.coroutines.runBlocking
import java.io.File

data class JiraGenerator(val config: SectionGeneratorConfig, val number: Int, val file: File) : SectionGenerator {
    private val api = JiraApi(config.client, jiraBaseUrl = config.parameterStore.getParameter("jiraBaseUrl"), apiToken = config.parameterStore.getParameter("apiToken"))
    private val issueCreator = IssueCreator(api)
    override fun generate(): List<Section> = runBlocking {

        file.readLines().map { issueCreator.create(it) }.map {
            val sectionCreatorConfig =
                SectionCreatorConfig(jiraIssue = it, keywordExtractor = config.keywordExtractor, api = api, issueCreator = issueCreator, number = SectionNumber(number), verbosity = config.verbosity)
            SectionCreator.create(sectionCreatorConfig)
        }.flatten()
    }


}