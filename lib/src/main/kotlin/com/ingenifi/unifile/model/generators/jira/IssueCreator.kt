package com.ingenifi.unifile.model.generators.jira

import com.ingenifi.unifile.formatter.jira.JiraApi

data class IssueCreator(val api: JiraApi) {
    fun create(issueKey: String, epic: Epic? = null): JiraIssue {
        val issueData = IssueData.from(issueKey, api.getIssue(issueKey))
        return when (issueData.type) {
            "epic" -> JiraIssue.epic(issueData)
            "story" -> if (epic != null) JiraIssue.epicStory(issueData, epic) else JiraIssue.story(issueData)
            "spike" -> if (epic != null) JiraIssue.epicSpike(issueData, epic) else JiraIssue.spike(issueData)
            "bug" -> if (epic != null) JiraIssue.epicBug(issueData, epic) else JiraIssue.bug(issueData)
            else -> throw IllegalArgumentException("type '${issueData.type}' not yet supported")
        }
    }
}