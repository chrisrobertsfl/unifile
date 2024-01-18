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

data class DocumentFormatterFactory(val properties: Map<String, String>, val keywordExtractor: KeywordExtractor, val client: HttpClient, val verbosity: Verbosity) :
    VerbosePrinting by VerbosePrinter(verbosity) {
    fun create(file: File): DocumentFormatter {
        return when (file.extension.lowercase()) {
            "confluence" -> ConfluencePagesFormatter(file = file, keywordExtractor = keywordExtractor, properties = properties, client = client, verbosity = verbosity)
            "jira" -> JiraFormatter(file = file, keywordExtractor = keywordExtractor, properties = properties, client = client, verbosity = verbosity)
            "json" -> JsonFormatter(file = file, keywordExtractor = keywordExtractor)
            "pdf" -> PdfFormatter(file = file, keywordExtractor = keywordExtractor)
            "ppt" -> PowerPointFormatter(file = file, keywordExtractor = keywordExtractor)
            "pptx" -> PowerPointFormatter(file = file, keywordExtractor = keywordExtractor)
            "txt" -> PlainTextFormatter(file = file, keywordExtractor = keywordExtractor)
            "xls" -> ExcelFormatter(file = file, keywordExtractor = keywordExtractor)
            "xlsx" -> ExcelFormatter(file = file, keywordExtractor = keywordExtractor)
            else -> throw IllegalArgumentException("Unknown formatter for extension ${file.extension.lowercase()}: given ${file.name}")
        }
    }
}