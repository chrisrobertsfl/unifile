package com.ingenifi.unifile.content.conversion

import com.ingenifi.unifile.content.JsonContent
import com.ingenifi.unifile.content.ContentType
import com.ingenifi.unifile.content.KeywordExtractor
import com.spire.presentation.Presentation
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File

data class PowerPoint(private val keywordExtractor: KeywordExtractor, private val type: ContentType) : ContentConverter {
    override fun convert(file: File): List<JsonContent> {

        val body = file.bodyFromPdf(file.asStream())
        return listOf(JsonContent(type = type, source = file.name, keywords = keywordExtractor.extract(body) + file.keywords(), body = body))
    }

    private fun File.asStream(): ByteArrayInputStream {
        val presentation = Presentation()
        presentation.loadFromFile(absolutePath)
        val bos = ByteArrayOutputStream()
        try {
            presentation.saveToFile(bos, com.spire.presentation.FileFormat.PDF)
        } catch (e: Exception) {
            println("Finally caught npe: ${e.message}")
        }
        return ByteArrayInputStream(bos.toByteArray())
    }
}