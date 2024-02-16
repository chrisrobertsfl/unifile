package com.ingenifi.unifile.model.generators.jira

import com.ingenifi.unifile.VerbosePrinter
import com.ingenifi.unifile.VerbosePrinting
import com.ingenifi.unifile.model.document.Section
import com.ingenifi.unifile.model.document.SectionNumber
import com.ingenifi.unifile.model.document.Sections
import com.ingenifi.unifile.model.generators.SectionsGenerator
import com.ingenifi.unifile.model.generators.SectionGeneratorConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import java.io.File

data class JiraGenerator(val config: SectionGeneratorConfig, val number: Int, val file: File) : SectionsGenerator, VerbosePrinting by VerbosePrinter(config.verbosity) {
    private val api = JiraApi(config.client, jiraBaseUrl = config.parameterStore.getParameter("jiraBaseUrl"), apiToken = config.parameterStore.getParameter("apiToken"))
    private val issueCreator = IssueCreator(api)
    private var numberOfFilesProcessed = 0


    override fun generate(): Sections {
        verbosePrint("Processing Jira file '${file.name}'")
        val sectionVerbosity = config.verbosity.increasingBy(by = 1)
        val sections = runBlocking {
            JiraFile(file = file).lines().map { line ->
                async(Dispatchers.IO) { // Launch in parallel using IO dispatcher
                    val issue = issueCreator.create(line)
                    val documentNumber = synchronized(this) {
                        val num = number + numberOfFilesProcessed
                        numberOfFilesProcessed += 1
                        num
                    }
                    val sectionCreatorConfig = SectionCreatorConfig(
                        jiraIssue = issue, keywordExtractor = config.keywordExtractor, api = api, issueCreator = issueCreator, number = SectionNumber(documentNumber), verbosity = sectionVerbosity
                    )
                    SectionCreator.create(sectionCreatorConfig)
                }
            }.awaitAll().flatten()
        }

        return Sections(list = sections, numberProcessed = numberOfFilesProcessed - 1)
    }


}