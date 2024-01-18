package com.ingenifi.unifile.formatter.confluence

import com.ingenifi.unifile.VerbosePrinter
import com.ingenifi.unifile.VerbosePrinting
import com.ingenifi.unifile.Verbosity
import com.ingenifi.unifile.formatter.DocumentFormatter
import com.ingenifi.unifile.formatter.KeywordExtractor
import io.ktor.client.*
import kotlinx.coroutines.runBlocking
import java.io.File

data class ConfluencePagesFormatter(private val client: HttpClient, private val file: File, private val keywordExtractor: KeywordExtractor, private val verbosity: Verbosity) : DocumentFormatter,
    VerbosePrinting by VerbosePrinter(verbosity) {
    private val api = ConfluenceApi(client, "TKMA5QX", "Kotlin2023!!") // TODO: System property
    private var lastNumber = 0

    override fun format(number: Int): String = runBlocking { formatSuspended(number) }
    private suspend fun formatSuspended(number: Int): String {
        lastNumber = number
        return file.readLines().map { ConfluenceLink.create(it, api) }
            .joinToString("\n") { link ->
                verbosePrint("Processing link: ${link.link}")
                ConfluencePageFormatter(link, keywordExtractor).format(lastNumber++)
            }
    }
    override fun lastNumber(): Int = lastNumber
}