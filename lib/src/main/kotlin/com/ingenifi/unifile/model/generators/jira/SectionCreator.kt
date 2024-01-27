package com.ingenifi.unifile.model.generators.jira

import com.ingenifi.unifile.model.document.Section
import com.ingenifi.unifile.model.document.SectionNumber

sealed interface SectionCreator {

    companion object {
        fun create(config: SectionCreatorConfig): List<Section> = when (config.jiraIssue) {
            is Epic -> EpicCreator(epic = config.jiraIssue, keywordExtractor = config.keywordExtractor, api = config.api, issueCreator = config.issueCreator, verbosity = config.verbosity).create(
                config.number
            )

            is Story -> StoryCreator(story = config.jiraIssue, keywordExtractor = config.keywordExtractor).create(config.number)
            is Spike -> SpikeCreator(spike = config.jiraIssue, keywordExtractor = config.keywordExtractor).create(config.number)
            is Bug -> BugCreator(bug = config.jiraIssue, keywordExtractor = config.keywordExtractor).create(config.number)
            is EpicStory -> EpicStoryCreator(story = config.jiraIssue, keywordExtractor = config.keywordExtractor, verbosity = config.verbosity).create(config.number)
            is EpicSpike -> EpicSpikeCreator(spike = config.jiraIssue, keywordExtractor = config.keywordExtractor, verbosity = config.verbosity).create(config.number)
            is EpicBug -> EpicBugCreator(bug = config.jiraIssue, keywordExtractor = config.keywordExtractor).create(config.number)
            else -> throw IllegalArgumentException("type not yet supported")
        }
    }

    fun create(sectionNumber: SectionNumber): List<Section>


}