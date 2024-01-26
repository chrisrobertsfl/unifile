package com.ingenifi.unifile.model.document

data class TemplateText(
    private val templateString: String,
    private val substitutions: Map<String, String>
) : BodyText {

    constructor(templateString: String, vararg substitutions: Pair<String, String>) :
            this(templateString, mapOf(*substitutions))

    override val content: String
        get() = Template(substitutions).substitute(templateString)
}