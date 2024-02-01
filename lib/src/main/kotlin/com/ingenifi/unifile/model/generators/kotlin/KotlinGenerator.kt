package com.ingenifi.unifile.model.generators.kotlin

import com.ingenifi.unifile.model.document.HeadingName
import com.ingenifi.unifile.model.document.Name
import com.ingenifi.unifile.model.document.TitleText
import com.ingenifi.unifile.model.generators.FileGenerator
import com.ingenifi.unifile.model.generators.SectionGenerator
import com.ingenifi.unifile.model.generators.SectionGeneratorConfig
import com.ingenifi.unifile.VerbosePrinter
import com.ingenifi.unifile.VerbosePrinting
import java.io.File

data class KotlinGenerator(val config: SectionGeneratorConfig, val number: Int, val file: File) :
    SectionGenerator by FileGenerator(config = config.copy(additionalKeywords = addKeywords(file)), number = number, file = file, headingName = headingName(file), title = TitleText.None),
    VerbosePrinting by VerbosePrinter(config.verbosity) {
    companion object {

        fun headingName(file: File): HeadingName {
            val typeName = file.nameWithoutExtension
            val classPattern = Regex("class\\s+$typeName\\b")
            val interfacePattern = Regex("interface\\s+$typeName\\b")
            var type = Name("Kotlin Code - ${file.name}") // Default type

            file.forEachLine { line ->
                when {
                    classPattern.containsMatchIn(line) -> {
                        type = Name("$typeName.kt - \"$typeName\" Class")
                        return@forEachLine // Break the loop
                    }

                    interfacePattern.containsMatchIn(line) -> {
                        type = Name("$typeName.kt - \"$typeName\" Interface")
                        return@forEachLine // Break the loop
                    }
                }
            }

            return type
        }

        private fun addKeywords(file: File): List<String> {
            val lines = file.readLines()
            val packageInfo = extractPackageInformation(lines)
            val importInfo = extractImportInformation(lines)
            return listOf("kotlin", packageInfo, file.nameWithoutExtension) + importInfo
        }

        private fun extractPackageInformation(lines: List<String>): String = lines.firstOrNull { it.startsWith("package") }?.substringAfter("package")?.trim() ?: ""
        private fun extractImportInformation(lines: List<String>): List<String> = lines.filter { it.trim().startsWith("import") }.map { it.substringAfter("import").trim() }
    }
}
