package com.ingenifi.unifile.formatter.jira

import com.ingenifi.unifile.VerbosePrinter
import com.ingenifi.unifile.VerbosePrinting
import com.ingenifi.unifile.Verbosity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking

data class IssueFactory(private val api: JiraApi, val verbosity: Verbosity) : VerbosePrinting by VerbosePrinter(verbosity) {

    fun create(key: String): Issue {
        verbosePrint("Retrieving issue $key from Jira")
        val data = api.getIssue(key)
        return when (type(data)) {
            "epic" -> Epic(key = key, title = data?.get(EPIC_TITLE) as String, introduction = data[EPIC_INTRO] as String, description = data[DESCRIPTION] as String, children = children(key))
            "story" -> Story(key, title = data?.get(TITLE) as String, description = data[DESCRIPTION] as String)
            "spike" -> Spike(key, title = data?.get(TITLE) as String, description = data[DESCRIPTION] as String)
            else -> throw IllegalArgumentException("no can handle")
        }
    }

    private fun children(key: String): MutableList<Issue> = runBlocking(Dispatchers.IO) {
        api.getChildren(key).map { childKey ->
            async {
                create(childKey)
            }
        }.awaitAll().toMutableList()
    }
    private fun type(data: Map<String, Any>?): String {

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
    val key: String
    val title : String
    val description : String
}

data class Epic(override val key: String, override val title: String, val introduction: String, override val description: String, val children: MutableList<Issue> = mutableListOf()) : Issue
data class Story(override val key: String, override val title: String, override val description: String) : Issue
data class Spike(override val key: String, override val title: String, override val description: String) : Issue
