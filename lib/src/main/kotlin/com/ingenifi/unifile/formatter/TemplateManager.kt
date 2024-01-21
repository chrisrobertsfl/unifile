package com.ingenifi.unifile.formatter

object TemplateManager {
    private val templateCache = mutableMapOf<String, String>()

    fun loadTemplate(templatePath: String): String {
        return templateCache.getOrPut(templatePath) {
            this::class.java.classLoader.getResource(templatePath)?.readText()
                ?: throw IllegalArgumentException("Template resource could not be read: $templatePath")
        }
    }
}

