package com.ingenifi.unifile.formatter.excel

import com.ingenifi.unifile.verbosity.VerbosePrinter
import com.ingenifi.unifile.verbosity.VerbosePrinting
import com.ingenifi.unifile.verbosity.Verbosity
import com.ingenifi.unifile.formatter.Delegate
import com.ingenifi.unifile.formatter.DocumentFormatter
import com.ingenifi.unifile.model.generators.KeywordExtractor
import com.ingenifi.unifile.formatter.toc.TableOfContents
import java.io.File

data class ExcelFormatter(val file: File, private val keywordExtractor: KeywordExtractor, private val verbosity: Verbosity, private val toc: TableOfContents) : DocumentFormatter,
    VerbosePrinting by VerbosePrinter(verbosity) {
    private var lastNumber = 0
    private val documents = mutableListOf<String>()

    private val source = ExcelSource(file)
    private val delegate = Delegate(source, keywordExtractor, toc)
    override fun format(number: Int): String {
        lastNumber = number
        val worksheetData = ExcelConverter().convert(file)
        verbosePrint("Processing workbook '${file.name}' with ${worksheetData.size} children")
        val childVerbosity = verbosity.increasingBy(1)
        var childNumber = 1
        val children = worksheetData.map {
            WorkSheetFormatter(worksheet = it.value, childNumber = childNumber++, keywordExtractor = keywordExtractor, toc = toc, verbosity = childVerbosity).format(number)
        }.joinToString("\n")
        return delegate.format(lastNumber, templatePath = "excel-document.tmpl", replacements = mapOf("children" to children), additionalKeywords = listOf("workbook"))

    }

    override fun lastNumber(): Int = lastNumber + 1
}

