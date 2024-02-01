package com.ingenifi.unifile.model.generators.jira

interface JiraIssue {
    val key: String
    val detail: String
    val title: String
    val type: String
    val keywords: List<String>

    companion object {
        fun epic(issueData: IssueData): Epic =
            Epic(key = issueData.key, detail = issueData.detail, summary = issueData.getEpicSummary(), title = issueData.getEpicTitle(), type = issueData.type, keywords = issueData.keywords)

        fun story(issueData: IssueData): Story = Story(key = issueData.key, detail = issueData.detail, title = issueData.getStoryTitle(), type = issueData.type, keywords = issueData.keywords)
        fun spike(issueData: IssueData): Spike = Spike(key = issueData.key, detail = issueData.detail, title = issueData.getSpikeTitle(), type = issueData.type, keywords = issueData.keywords)
        fun bug(issueData: IssueData): Bug = Bug(key = issueData.key, detail = issueData.detail, title = issueData.getBugTitle(), type = issueData.type, keywords = issueData.keywords)
        fun epicStory(issueData: IssueData, epic: Epic): EpicStory =
            EpicStory(key = issueData.key, detail = issueData.detail, title = issueData.getBugTitle(), epic = epic, type = issueData.asEpicChildType(), keywords = epic.keywords)

        fun epicSpike(issueData: IssueData, epic: Epic): EpicSpike =
            EpicSpike(key = issueData.key, detail = issueData.detail, title = issueData.getBugTitle(), epic = epic, type = issueData.asEpicChildType(), keywords = epic.keywords)

        fun epicBug(issueData: IssueData, epic: Epic): EpicBug =
            EpicBug(key = issueData.key, detail = issueData.detail, title = issueData.getBugTitle(), epic = epic, type = issueData.asEpicChildType(), keywords = epic.keywords)

        private fun IssueData.asEpicChildType() = "epic child $type"
    }

}

interface EpicChild : JiraIssue {
    val epic: Epic
}

data class Epic(
    override val key: String,
    override val detail: String,
    val summary: String,
    override val title: String,
    override val type: String,
    override val keywords: List<String>,
    val children: List<JiraIssue> = listOf()
) : JiraIssue

data class EpicBug(override val key: String, override val detail: String, override val title: String, override val epic: Epic, override val type: String, override val keywords: List<String>) :
    EpicChild

data class EpicSpike(override val key: String, override val detail: String, override val title: String, override val epic: Epic, override val type: String, override val keywords: List<String>) :
    EpicChild

data class EpicStory(override val key: String, override val detail: String, override val title: String, override val epic: Epic, override val type: String, override val keywords: List<String>) :
    EpicChild

data class Spike(override val key: String, override val detail: String, override val title: String, override val type: String, override val keywords: List<String>) : JiraIssue
data class Story(override val key: String, override val detail: String, override val title: String, override val type: String, override val keywords: List<String>) : JiraIssue
data class Bug(override val key: String, override val detail: String, override val title: String, override val type: String, override val keywords: List<String>) : JiraIssue