package com.ingenifi.unifile.model.document

data class Template(private val placeholders: Map<String, String>) {
    constructor(vararg mappings: Pair<String, String>) : this(mapOf(*mappings))
    fun substitute(templateString: String): String {
        var substitutedString = templateString
        placeholders.forEach { (placeholder, value) ->
            substitutedString = substitutedString.replace("{$placeholder}", value)
        }
        return substitutedString
    }
}