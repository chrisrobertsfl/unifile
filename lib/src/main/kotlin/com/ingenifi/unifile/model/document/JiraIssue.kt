package com.ingenifi.unifile.model.document

interface JiraIssue {
    val key: String
    val detail: String
    val title: String

    companion object {
        fun epic(issueData: IssueData): Epic =
            Epic(key = issueData.key, detail = issueData.detail, summary = issueData.getEpicSummary(), title = issueData.getEpicTitle())
        fun story(issueData: IssueData): Story = Story(key = issueData.key, detail = issueData.detail, title = issueData.getStoryTitle())
        fun spike(issueData: IssueData): Spike = Spike(key = issueData.key, detail = issueData.detail, title = issueData.getSpikeTitle())
        fun bug(issueData: IssueData): Bug = Bug(key = issueData.key, detail = issueData.detail, title = issueData.getBugTitle())
        fun epicStory(issueData: IssueData, epic: Epic): EpicStory =
            EpicStory(key = issueData.key, detail = issueData.detail, title = issueData.getBugTitle(), epic = epic)
        fun epicSpike(issueData: IssueData, epic: Epic): EpicSpike =
            EpicSpike(key = issueData.key, detail = issueData.detail, title = issueData.getBugTitle(), epic = epic)
        fun epicBug(issueData: IssueData, epic: Epic): EpicBug =
            EpicBug(key = issueData.key, detail = issueData.detail, title = issueData.getBugTitle(), epic = epic)

    }
}