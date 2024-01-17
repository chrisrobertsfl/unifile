package com.ingenifi.unifile.formatter.confluence

import com.ingenifi.unifile.formatter.DocumentFormatter
import com.ingenifi.unifile.formatter.KeywordExtractor
import io.ktor.client.*
import kotlinx.coroutines.runBlocking
import java.io.File

data class ConfluencePagesFormatter(private val client: HttpClient, private val file : File, private val keywordExtractor: KeywordExtractor) : DocumentFormatter {
    private val api = ConfluenceApi(client, "TKMA5QX", "Kotlin2023!!")
    private var lastNumber = 0

    override fun format(number: Int): String = runBlocking { formatSuspended(number) }
    suspend fun formatSuspended(number: Int): String {
        lastNumber = number
        return file.readLines().map{ ConfluenceLink.create(it, api) }
            .map { ConfluencePageFormatter(it, keywordExtractor).format(lastNumber++) }
            .joinToString("\n")
    }
    override fun lastNumber(): Int = lastNumber
}