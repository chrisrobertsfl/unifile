package com.ingenifi.unifile.content.conversion

import com.ingenifi.unifile.content.Content
import com.ingenifi.unifile.content.ContentType
import com.ingenifi.unifile.content.KeywordExtractor
import com.spire.pdf.PdfDocument
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File

data class Pdf(private val keywordExtractor: KeywordExtractor) : ContentConverter {
    override fun convert(file: File): List<Content> {
        val body = file.bodyFromPdf(file.asStream())
        return listOf(Content(type = ContentType.PDF, source = file.name, keywords = keywordExtractor.extract(body) + file.keywords(), body = body))
    }

    private fun File.asStream(): ByteArrayInputStream {
        val pdf = PdfDocument()
        pdf.loadFromFile(absolutePath)
        val bos = ByteArrayOutputStream()
        pdf.saveToStream(bos, com.spire.pdf.FileFormat.PDF)
        return ByteArrayInputStream(bos.toByteArray())
    }
}