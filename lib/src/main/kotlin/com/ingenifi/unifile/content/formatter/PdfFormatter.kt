package com.ingenifi.unifile.content.formatter

import com.ingenifi.unifile.PdfConverter
import com.ingenifi.unifile.content.KeywordExtractor
import java.io.File

data class PdfFormatter(val file: File, private val keywordExtractor: KeywordExtractor) : DocumentFormatter {
    private val delegate = Delegate(PdfSource(file), keywordExtractor)
    override fun format(number: Int): String = delegate.format(number, templatePath = "pdf-document.tmpl", replacements = mapOf<String, String>("filename" to file.name))
    override fun lastNumber(): Int = delegate.lastNumber()
}

data class PdfSource(val file: File) : Source {
    override fun description(): String = file.toText()
    override fun title(): String = file.name
    private fun File.toText(): String = PdfConverter().convert(this)

}