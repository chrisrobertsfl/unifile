package com.ingenifi.unifile.content.conversion

import com.ingenifi.unifile.content.Content
import com.ingenifi.unifile.content.ContentType
import com.ingenifi.unifile.content.KeywordExtractor
import java.io.File

data class PlainText(private val keywordExtractor: KeywordExtractor) : ContentConverter {
    override fun convert(file: File): List<Content> {
        val body = file.readText()
        return listOf(Content(type = ContentType.PLAIN_TEXT, source = file.name, keywords = keywordExtractor.extract(body) + file.keywords(), body = body))
    }
}