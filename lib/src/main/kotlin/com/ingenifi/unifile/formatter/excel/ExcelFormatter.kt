package com.ingenifi.unifile.formatter.excel

import com.ingenifi.unifile.VerbosePrinter
import com.ingenifi.unifile.VerbosePrinting
import com.ingenifi.unifile.Verbosity
import com.ingenifi.unifile.formatter.Delegate
import com.ingenifi.unifile.formatter.DocumentFormatter
import com.ingenifi.unifile.formatter.EntrySource
import com.ingenifi.unifile.formatter.KeywordExtractor
import com.ingenifi.unifile.formatter.toc.HeadingNumber
import com.ingenifi.unifile.formatter.toc.SectionNumber
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
        val workBookName = file.name
        verbosePrint("Processing workbook '${workBookName}' with ${worksheetData.size} children")
        val childVerbosity = verbosity.increasingBy(1)
        var childNumber = 1
        val children = worksheetData.map {
            WorkSheetFormatter(
                name = it.key, description = it.value, childNumber = childNumber++, workbookName = workBookName, keywordExtractor = keywordExtractor, toc = toc, verbosity = childVerbosity
            ).format(number)
        }.joinToString("\n")
        return delegate.format(lastNumber, templatePath = "excel-document.tmpl", replacements = mapOf("children" to children), additionalKeywords = listOf("workbook"))

    }

    override fun lastNumber(): Int = lastNumber + 1
}

data class WorkSheetFormatter(
    val name: String, val description: String, val workbookName: String, val childNumber: Int, val keywordExtractor: KeywordExtractor, val toc: TableOfContents, val verbosity: Verbosity
) : DocumentFormatter, VerbosePrinting by VerbosePrinter(verbosity) {
    private var lastNumber = 0
    override fun format(number: Int): String {
        lastNumber = number
        val source = WorkSheetSource(title = name, description = description, headingNumber = SectionNumber(listOf(number, childNumber)), workbookName = workbookName)
        val delegate = Delegate(source, keywordExtractor, toc)
        verbosePrint("Processing worksheet '$name'")
        lastNumber = number
        return delegate.format(
            number, templatePath = "worksheet-document.tmpl", replacements = replacements(source), additionalKeywords = additionalKeywords(source)
        )
    }

    override fun lastNumber(): Int = lastNumber + 1

    private fun replacements(source: WorkSheetSource) = mapOf<String, String>("headingNumber" to source.headingNumber.asString(), "workbookName" to workbookName)
    private fun additionalKeywords(source: WorkSheetSource): List<String> {
        val keywords = mutableListOf<String>()
        keywords.addAll(keywordExtractor.extract(description, percentage = 0.3))
        keywords.add("worksheet")
        keywords.add(source.workbookName)
        return keywords

    }
}

data class WorkSheetSource(val title: String, val description: String, override val headingNumber: HeadingNumber, val workbookName: String) : EntrySource {
    override fun description(): String = description
    override fun title(): String = title
}

