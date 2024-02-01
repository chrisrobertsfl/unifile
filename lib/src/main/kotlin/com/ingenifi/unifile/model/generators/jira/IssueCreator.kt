package com.ingenifi.unifile.model.generators.jira

data class IssueCreator(val api: JiraApi) {
    fun create(jiraLine: JiraLine): JiraIssue {
        val rawMap = api.getIssue(jiraLine.key)
        val issueData = IssueData.from(jiraLine, rawMap)
        val jiraIssue = when (issueData.type) {
            "epic" -> JiraIssue.epic(issueData)
            "story" -> JiraIssue.story(issueData)
            "spike" -> JiraIssue.spike(issueData)
            "bug" -> JiraIssue.bug(issueData)
            else -> throw IllegalArgumentException("type '${issueData.type}' not yet supported")
        }
        return jiraIssue
    }

    fun createEpicChild(key: String, epic: Epic): EpicChild {
        val rawMap = api.getIssue(key)
        val issueData = IssueData.from(key, rawMap)
        val jiraIssue = when (issueData.type) {
            "story" -> JiraIssue.epicStory(issueData, epic)
            "spike" -> JiraIssue.epicSpike(issueData, epic)
            "bug" -> JiraIssue.epicBug(issueData, epic)
            else -> throw IllegalArgumentException("type '${issueData.type}' not yet supported")
        }
        return jiraIssue
    }
}