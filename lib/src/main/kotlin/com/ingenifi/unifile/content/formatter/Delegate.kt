package com.ingenifi.unifile.content.formatter

import com.ingenifi.unifile.content.KeywordExtractor

class Delegate(private val source: Source, private val keywordExtractor: KeywordExtractor) {
    private var lastNumber = 0

    companion object {
        private val templateCache = mutableMapOf<String, String>()

        private fun loadTemplate(templatePath: String): String {
            return templateCache.getOrPut(templatePath) {
                Delegate::class.java.classLoader.getResource(templatePath)?.readText()
                    ?: throw IllegalArgumentException("Template resource could not be read: $templatePath")
            }
        }
    }

    fun format(number: Int, templatePath: String, replacements: Map<String, String> = mapOf(), extractPercentage: Double = 0.025): String {
        val description = source.description()
        val title = source.title()
        val keywords = mutableSetOf<String>()
        keywords.addAll(keywordExtractor.extract(text = description, percentage = extractPercentage))

        var template = loadTemplate(templatePath)

        // Standard and dynamic replacements
        template = template.replace("{number}", number.toString())
            .replace("{title}", title)
            .replace("{description}", description)
            .replace("{keywords}", keywords.joinToString(", "))
        replacements.forEach { (key, value) -> template = template.replace("{$key}", value) }

        lastNumber = number + 1
        return template
    }

    fun lastNumber(): Int = lastNumber
}