package com.ingenifi.unifile.formatter.jira

data class IssueFactory(private val api : JiraApi, private val verbose : Boolean = false) {

    fun create(key : String) : Issue {
        val data =  api.getIssue(key)
        if (verbose) println("o Processing issue $key")
        return when (type(data)) {
            "epic" -> Epic(key = key, title = data?.get(EPIC_TITLE) as String, introduction = data[EPIC_INTRO] as String, description = data[DESCRIPTION] as String, children = children(key))
            "story" -> Story(key, title = data?.get(TITLE) as String, description = data[DESCRIPTION] as String)
            "spike" -> Spike(key, title = data?.get(TITLE) as String, description = data[DESCRIPTION] as String)
                else -> throw IllegalArgumentException("no can handle")
        }
    }

     fun children(key : String) : MutableList<Issue> = api.getChildren(key)
         .map { create(it) }
         .toMutableList()

    private fun type(data : Map<String, Any>?)  : String {
        val issueTypeMap = data?.get(ISSUE_TYPE) as Map<String, Any>
        val type = issueTypeMap["name"] as String
        return type.lowercase()
    }

    companion object {
        val ISSUE_TYPE = "issuetype"
        val EPIC_TITLE = "customfield_10304"
        val EPIC_INTRO = "summary"
        val DESCRIPTION = "description"
        val TITLE = "summary"
    }

}

sealed interface Issue {
    val key : String
}
data class Epic(override val key : String, val title : String, val introduction : String, val description : String, val children : MutableList<Issue> = mutableListOf()) : Issue
data class Story(override val key : String, val title : String, val description : String) : Issue
data class Spike(override val key : String, val title : String, val description : String) : Issue



 interface IssueDocumentFormatter {
    fun format(): String

}