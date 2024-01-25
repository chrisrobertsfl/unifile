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

data class WorkSheetFormatter(val worksheet: WorkSheet, val childNumber: Int, val keywordExtractor: KeywordExtractor, val toc: TableOfContents, val verbosity: Verbosity
) : DocumentFormatter, VerbosePrinting by VerbosePrinter(verbosity) {
    private var lastNumber = 0
    override fun format(number: Int): String {
        lastNumber = number
        val source = WorkSheetSource(worksheet = worksheet, headingNumber = SectionNumber(listOf(number, childNumber)))
        val delegate = Delegate(source, keywordExtractor, toc)
        verbosePrint("Processing worksheet '${source.title()}' from workbook '${source.workbookName()}")
        lastNumber = number
        return delegate.format(
            number, templatePath = "worksheet-document.tmpl", replacements = replacements(source), additionalKeywords = additionalKeywords(source)
        )
    }

    override fun lastNumber(): Int = lastNumber + 1

    private fun replacements(source: WorkSheetSource) = mapOf<String, String>("headingNumber" to source.headingNumber.asString(), "workbookName" to source.workbookName())
    private fun additionalKeywords(source: WorkSheetSource): List<String> {
        val keywords = mutableListOf<String>()
        keywords.addAll(source.header())
        keywords.add("worksheet")
        keywords.add(source.workbookName())
        return keywords

    }
}

data class WorkSheetSource(val worksheet: WorkSheet, override val headingNumber: HeadingNumber) : EntrySource {
    override fun description(): String = worksheet.toFullCsv()
    override fun title(): String = worksheet.title

    fun header() = worksheet.header
    fun workbookName() : String = worksheet.workbookName
}

