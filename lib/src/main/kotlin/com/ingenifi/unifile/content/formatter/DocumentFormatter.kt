package com.ingenifi.unifile.content.formatter

import com.ingenifi.unifile.content.KeywordExtractor
import io.ktor.client.*
import java.io.File

interface DocumentFormatter {

     companion object {
         fun from(file : File, keywordExtractor: KeywordExtractor, client : HttpClient) : DocumentFormatter {
             return when(file.extension.lowercase()) {
                 "clink" -> ConfluencePagesFormatter(file = file, keywordExtractor = keywordExtractor, client = client)
                 "json" -> JsonFormatter(file, keywordExtractor)
                 "pdf" -> PdfFormatter(file, keywordExtractor)
                 "ppt" -> PowerPointFormatter(file, keywordExtractor)
                 "pptx" -> PowerPointFormatter(file, keywordExtractor)
                 "txt" -> PlainTextFormatter(file, keywordExtractor)
                 "xls" -> ExcelFormatter(file, keywordExtractor)
                 "xlsx" -> ExcelFormatter(file, keywordExtractor)
                 else -> throw IllegalArgumentException("Unknown formatter for $file")
             }
         }
     }
    fun format(number: Int): String
    fun lastNumber(): Int
}