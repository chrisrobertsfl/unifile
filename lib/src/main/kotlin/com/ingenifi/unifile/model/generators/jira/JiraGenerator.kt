package com.ingenifi.unifile.model.generators.jira

import com.ingenifi.unifile.VerbosePrinter
import com.ingenifi.unifile.VerbosePrinting
import com.ingenifi.unifile.model.document.Section
import com.ingenifi.unifile.model.document.SectionNumber
import com.ingenifi.unifile.model.generators.SectionGenerator
import com.ingenifi.unifile.model.generators.SectionGeneratorConfig
import kotlinx.coroutines.runBlocking
import java.io.File

data class JiraGenerator(val config: SectionGeneratorConfig, val number: Int, val file: File) : SectionGenerator, VerbosePrinting by VerbosePrinter(config.verbosity) {
    private val api = JiraApi(config.client, jiraBaseUrl = config.parameterStore.getParameter("jiraBaseUrl"), apiToken = config.parameterStore.getParameter("apiToken"))
    private val issueCreator = IssueCreator(api)
    override fun generate(): List<Section> = runBlocking {
        verbosePrint("Processing Jira file '${file.name}'")
        val sectionVerbosity = config.verbosity.increasingBy(by = 1)
        JiraFile(file = file).lines().map { issueCreator.create(it) }.mapIndexed { index, it ->
            val documentNumber = number + index
            val sectionCreatorConfig = SectionCreatorConfig(
                jiraIssue = it, keywordExtractor = config.keywordExtractor, api = api, issueCreator = issueCreator, number = SectionNumber(documentNumber), verbosity = sectionVerbosity
            )
            SectionCreator.create(sectionCreatorConfig)
        }.flatten()
    }
}