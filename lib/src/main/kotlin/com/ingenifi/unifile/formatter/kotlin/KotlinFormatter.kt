package com.ingenifi.unifile.formatter.kotlin

import com.ingenifi.unifile.formatter.Delegate
import com.ingenifi.unifile.formatter.DocumentFormatter
import com.ingenifi.unifile.KeywordExtractor
import com.ingenifi.unifile.formatter.toc.TableOfContents
import java.io.File

data class KotlinFormatter(val file: File, private val keywordExtractor: KeywordExtractor, val toc : TableOfContents) : DocumentFormatter {
    private val delegate = Delegate(KotlinSource(file), keywordExtractor, toc)
    override fun format(number: Int): String {
        val keywords = mutableListOf<String>()
        keywords.add("kotlin")
        keywords.add(getPackageInformation(file))
        keywords.add(file.name.substringBeforeLast("."))
        return delegate.format(number, templatePath = "kotlin-document.tmpl", replacements = mapOf<String, String>("filename" to file.name), additionalKeywords = keywords)
    }

    private fun getPackageInformation(file: File): String {
        val packageLine = file.readLines().first { it.startsWith("package") }
        return packageLine.substringAfter("package").trim()
    }

    override fun lastNumber(): Int = delegate.lastNumber()
}