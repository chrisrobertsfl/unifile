package com.ingenifi.unifile.formatter

import com.ingenifi.unifile.VerbosePrinter
import com.ingenifi.unifile.VerbosePrinting
import com.ingenifi.unifile.Verbosity
import com.ingenifi.unifile.formatter.confluence.ConfluencePagesFormatter
import com.ingenifi.unifile.formatter.excel.ExcelFormatter
import com.ingenifi.unifile.formatter.jira.JiraFormatter
import com.ingenifi.unifile.formatter.json.JsonFormatter
import com.ingenifi.unifile.formatter.pdf.PdfFormatter
import com.ingenifi.unifile.formatter.plaintext.PlainTextFormatter
import com.ingenifi.unifile.formatter.powerpoint.PowerPointFormatter
import io.ktor.client.*
import java.io.File

data class DocumentFormatterFactory(val keywordExtractor: KeywordExtractor, val client: HttpClient, val verbosity: Verbosity) : VerbosePrinting by VerbosePrinter(verbosity) {
    fun create(file: File): DocumentFormatter {
        return when (file.extension.lowercase()) {
            "confluence" -> ConfluencePagesFormatter(file = file, keywordExtractor = keywordExtractor, client = client, verbosity = verbosity)
            "jira" -> JiraFormatter(file = file, keywordExtractor = keywordExtractor, client = client, verbosity = verbosity)
            "json" -> JsonFormatter(file, keywordExtractor)
            "pdf" -> PdfFormatter(file, keywordExtractor)
            "ppt" -> PowerPointFormatter(file, keywordExtractor)
            "pptx" -> PowerPointFormatter(file, keywordExtractor)
            "txt" -> PlainTextFormatter(file, keywordExtractor)
            "xls" -> ExcelFormatter(file, keywordExtractor)
            "xlsx" -> ExcelFormatter(file, keywordExtractor)
            else -> throw IllegalArgumentException("Unknown formatter for extension ${file.extension.lowercase()}: given ${file.name}")
        }
    }
}