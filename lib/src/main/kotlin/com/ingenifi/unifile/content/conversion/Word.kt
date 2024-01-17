package com.ingenifi.unifile.content.conversion

import com.ingenifi.unifile.content.JsonContent
import com.ingenifi.unifile.content.ContentType
import com.ingenifi.unifile.content.KeywordExtractor
import com.spire.doc.Document
import com.spire.doc.FileFormat
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File

data class Word(private val keywordExtractor: KeywordExtractor, private val type: ContentType) : ContentConverter {
    override fun convert(file: File): List<JsonContent> {

        val body = file.bodyFromPdf(file.asStream())
        return listOf(JsonContent(type = type, source = file.name, keywords = keywordExtractor.extract(body) + file.keywords(), body = body))
    }
    private fun File.asStream(): ByteArrayInputStream {
        val document = Document()
        document.loadFromFile(absolutePath)
        val bos = ByteArrayOutputStream()
        document.saveToStream(bos, FileFormat.PDF)
        return ByteArrayInputStream(bos.toByteArray())
    }
}