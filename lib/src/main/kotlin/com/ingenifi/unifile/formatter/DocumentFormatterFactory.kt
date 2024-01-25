package com.ingenifi.unifile.formatter

import com.ingenifi.unifile.ParameterStore
import com.ingenifi.unifile.VerbosePrinter
import com.ingenifi.unifile.VerbosePrinting
import com.ingenifi.unifile.Verbosity
import com.ingenifi.unifile.formatter.confluence.ConfluencePagesFormatter
import com.ingenifi.unifile.formatter.conversation.TranscriptFormatter
import com.ingenifi.unifile.formatter.excel.ExcelFormatter
import com.ingenifi.unifile.formatter.jira.JiraFormatter
import com.ingenifi.unifile.formatter.json.JsonFormatter
import com.ingenifi.unifile.formatter.kotlin.KotlinFormatter
import com.ingenifi.unifile.formatter.pdf.PdfFormatter
import com.ingenifi.unifile.formatter.plaintext.PlainTextFormatter
import com.ingenifi.unifile.formatter.powerpoint.PowerPointFormatter
import com.ingenifi.unifile.formatter.properites.PropertiesFormatter
import com.ingenifi.unifile.formatter.properites.TemplateFormatter
import com.ingenifi.unifile.formatter.toc.TableOfContents
import com.ingenifi.unifile.formatter.word.WordFormatter
import com.ingenifi.unifile.formatter.xml.XmlFormatter
import io.ktor.client.*
import java.io.File

data class DocumentFormatterFactory(val parameterStore: ParameterStore, val keywordExtractor: KeywordExtractor, val client: HttpClient, val toc: TableOfContents, val verbosity: Verbosity) :
    VerbosePrinting by VerbosePrinter(verbosity) {
    fun create(file: File): DocumentFormatter {
        return when (file.extension.lowercase()) {
            "confluence" -> ConfluencePagesFormatter(file = file, keywordExtractor = keywordExtractor, parameterStore = parameterStore, client = client, toc = toc, verbosity = verbosity)
            "doc" -> WordFormatter(file = file, keywordExtractor = keywordExtractor, toc = toc)
            "docx" -> WordFormatter(file = file, keywordExtractor = keywordExtractor, toc = toc)
            "jira" -> JiraFormatter(file = file, keywordExtractor = keywordExtractor, parameterStore = parameterStore, client = client, toc = toc, verbosity = verbosity)
            "json" -> JsonFormatter(file = file, keywordExtractor = keywordExtractor, toc = toc)
            "kt" -> KotlinFormatter(file = file, keywordExtractor = keywordExtractor, toc = toc)
            "pdf" -> PdfFormatter(file = file, keywordExtractor = keywordExtractor, toc = toc)
            "ppt" -> PowerPointFormatter(file = file, keywordExtractor = keywordExtractor, toc = toc)
            "pptx" -> PowerPointFormatter(file = file, keywordExtractor = keywordExtractor, toc = toc)
            "properties" -> PropertiesFormatter(file = file, keywordExtractor = keywordExtractor, toc = toc)
            "tmpl" -> TemplateFormatter(file = file, keywordExtractor = keywordExtractor,  toc = toc)
            "transcript" -> TranscriptFormatter(file = file, keywordExtractor = keywordExtractor,  toc = toc)
            "txt" -> PlainTextFormatter(file = file, keywordExtractor = keywordExtractor, toc = toc)
            "xls" -> ExcelFormatter(file = file, keywordExtractor = keywordExtractor, toc = toc, verbosity = verbosity)
            "xlsx" -> ExcelFormatter(file = file, keywordExtractor = keywordExtractor, toc = toc, verbosity = verbosity)
            "xml" -> XmlFormatter(file = file, keywordExtractor = keywordExtractor, toc = toc)
            else -> throw IllegalArgumentException("Unknown formatter for extension ${file.extension.lowercase()}: given ${file.name}")
        }
    }
}