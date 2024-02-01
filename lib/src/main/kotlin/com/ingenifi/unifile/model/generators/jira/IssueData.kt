package com.ingenifi.unifile.model.generators.jira

data class IssueData(private val rawMap: Map<String, Any>?, val key: String, val type: String, val detail: String, val keywords : List<String> = listOf()) {
    fun getStoryTitle() = getNonEpicTitle()
    fun getSpikeTitle() = getNonEpicTitle()
    fun getBugTitle() = getNonEpicTitle()
    private fun getNonEpicTitle() = rawMap?.get("summary") as String
    fun getEpicTitle(): String = rawMap?.get("customfield_10304") as String
    fun getEpicSummary(): String = rawMap?.get("summary") as String

    companion object {
        fun from(jiraLine: JiraLine, rawMap: Map<String, Any>?): IssueData {
            return IssueData(rawMap = rawMap, key = jiraLine.key, type = parseType(rawMap), detail = rawMap?.get("description") as String, keywords = jiraLine.keywords)
        }
        fun from(key: String, rawMap: Map<String, Any>?): IssueData {
            return IssueData(rawMap = rawMap, key = key, type = parseType(rawMap), detail = rawMap?.get("description") as String)
        }

        @Suppress("UNCHECKED_CAST")
        private fun parseType(rawMap: Map<String, Any>?): String {
            val issueTypeMap = rawMap?.get("issuetype") as Map<String, Any>
            val type = issueTypeMap["name"] as String
            return type.lowercase()
        }
    }
}