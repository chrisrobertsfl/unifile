package com.ingenifi.unifile.formatter.excel

import com.ingenifi.unifile.VerbosePrinter
import com.ingenifi.unifile.VerbosePrinting
import com.ingenifi.unifile.Verbosity
import com.ingenifi.unifile.formatter.Delegate
import com.ingenifi.unifile.formatter.DocumentFormatter
import com.ingenifi.unifile.formatter.KeywordExtractor
import com.ingenifi.unifile.formatter.toc.HeadingNumber
import com.ingenifi.unifile.formatter.toc.TableOfContents

data class WorkSheetFormatter(val worksheet: WorkSheet, val childNumber: Int, val keywordExtractor: KeywordExtractor, val toc: TableOfContents, val verbosity: Verbosity
) : DocumentFormatter, VerbosePrinting by VerbosePrinter(verbosity) {
    private var lastNumber = 0
    override fun format(number: Int): String {
        lastNumber = number
        val source = WorkSheetSource(worksheet = worksheet, headingNumber = HeadingNumber(listOf(number, childNumber)))
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