package com.ingenifi.unifile.formatter

class Template(private val templatePath: String) {

    private val content: String by lazy {
        TemplateManager.loadTemplate(templatePath)
    }

    fun applyReplacements(replacements: Map<String, String>): String {
        var result = content
        replacements.forEach { (key, value) ->
            result = result.replace("{$key}", value)
        }
        return result
    }
}
