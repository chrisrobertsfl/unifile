package com.ingenifi.unifile.formatter

import com.ingenifi.unifile.formatter.confluence.ConfluencePagesFormatter
import com.ingenifi.unifile.formatter.excel.ExcelFormatter
import com.ingenifi.unifile.formatter.jira.JiraFormatter
import com.ingenifi.unifile.formatter.json.JsonFormatter
import com.ingenifi.unifile.formatter.pdf.PdfFormatter
import com.ingenifi.unifile.formatter.plaintext.PlainTextFormatter
import com.ingenifi.unifile.formatter.powerpoint.PowerPointFormatter
import io.ktor.client.*
import java.io.File

interface DocumentFormatter {

     companion object {
         fun from(file : File, keywordExtractor: KeywordExtractor, client : HttpClient) : DocumentFormatter {
             return when(file.extension.lowercase()) {
                 "clink" -> ConfluencePagesFormatter(file = file, keywordExtractor = keywordExtractor, client = client)
                 "jira" -> JiraFormatter(file = file, keywordExtractor = keywordExtractor, client = client)
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
    fun format(number: Int): String
    fun lastNumber(): Int
}