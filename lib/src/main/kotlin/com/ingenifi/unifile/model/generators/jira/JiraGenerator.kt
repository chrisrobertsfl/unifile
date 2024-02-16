package com.ingenifi.unifile.model.generators.jira

import com.ingenifi.unifile.VerbosePrinter
import com.ingenifi.unifile.VerbosePrinting
import com.ingenifi.unifile.model.document.Section
import com.ingenifi.unifile.model.document.SectionNumber
import com.ingenifi.unifile.model.document.Sections
import com.ingenifi.unifile.model.generators.SectionsGenerator
import com.ingenifi.unifile.model.generators.SectionGeneratorConfig
import java.io.File

data class JiraGenerator(val config: SectionGeneratorConfig, val number: Int, val file: File) : SectionsGenerator, VerbosePrinting by VerbosePrinter(config.verbosity) {
    private val api = JiraApi(config.client, jiraBaseUrl = config.parameterStore.getParameter("jiraBaseUrl"), apiToken = config.parameterStore.getParameter("apiToken"))
    private val issueCreator = IssueCreator(api)
    private var numberOfFilesProcessed = 0

    override fun generate(): Sections {
        verbosePrint("Processing Jira file '${file.name}'")
        val sectionVerbosity = config.verbosity.increasingBy(by = 1)
        val list =  JiraFile(file = file).lines().map { issueCreator.create(it) }.map {
            val documentNumber = number + numberOfFilesProcessed
            val sectionCreatorConfig = SectionCreatorConfig(
                jiraIssue = it, keywordExtractor = config.keywordExtractor, api = api, issueCreator = issueCreator, number = SectionNumber(documentNumber), verbosity = sectionVerbosity
            )
            val sections = SectionCreator.create(sectionCreatorConfig)
            numberOfFilesProcessed += 1
            sections
        }.flatten()
        return Sections(list = list, numberProcessed = numberOfFilesProcessed - 1)
    }

}