package com.ingenifi.unifile.model.document

data class ResourceTemplateText(val resource: String, val substitutions: Map<String, String>) : BodyText by TemplateText(
    ResourceTemplateText::class.java.classLoader.getResource(resource)?.readText() ?: throw IllegalArgumentException("Template resource could not be read: $resource"), substitutions
) {
    constructor(resource: String, vararg substitutions: Pair<String, String>) : this(resource, mapOf(*substitutions))
}
