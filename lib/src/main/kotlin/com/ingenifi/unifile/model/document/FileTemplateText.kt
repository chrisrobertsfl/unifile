package com.ingenifi.unifile.model.document

import java.io.File

data class FileTemplateText(val file: File, val substitutions: Map<String, String>) : BodyText by TemplateText(file.readText(), substitutions) {
    constructor(file: File, vararg substitutions: Pair<String, String>) : this(file, mapOf(*substitutions))
}
