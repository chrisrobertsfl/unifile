package com.ingenifi.unifile.content.conversion

import com.ingenifi.unifile.content.Content
import com.ingenifi.unifile.content.JsonContent
import com.ingenifi.unifile.content.ContentType
import com.ingenifi.unifile.content.KeywordExtractor
import com.spire.pdf.PdfDocument
import org.apache.pdfbox.io.RandomAccessReadBuffer
import org.apache.pdfbox.pdfparser.PDFParser
import org.apache.pdfbox.text.PDFTextStripper
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.InputStream

sealed interface ContentConverter {
    fun convert(file: File): List<Content>

    companion object {
        fun from(extension: String, keywordExtractor: KeywordExtractor, verbose : Boolean = false): ContentConverter = when (extension.lowercase()) {
            "clink" -> ConfluenceLinkConverter(keywordExtractor, username = "TKMA5QX", password = "Kotlin2023!!", verbose = verbose)
            "doc" -> Word(keywordExtractor, ContentType.DOC)
            "docx" -> Word(keywordExtractor, ContentType.DOCX)
            "json" -> Json(keywordExtractor)
            "pdf" -> Pdf(keywordExtractor)
            "ppt" -> PowerPoint(keywordExtractor, ContentType.PPT)
            "pptx" -> PowerPoint(keywordExtractor, ContentType.PPTX)
            "txt" -> PlainText(keywordExtractor)
            "xls" -> Excel(keywordExtractor, ContentType.XLS)
            "xlsx" -> Excel(keywordExtractor, ContentType.XLSX)

            else -> Unknown
        }
        val TEXT_STRIPPER = PDFTextStripper()
    }

    fun File.keywords(): List<String> {
        val nameKeywords = name.substringBeforeLast(".").split(Regex("\\W+")).filter { it.isNotBlank() }
        val pathKeywords = absolutePath.split(Regex("\\W+")).filter { it.isNotBlank() }

        return (nameKeywords + pathKeywords).distinct()
    }

    fun File.bodyFromPdf(inputStream: InputStream): String = RandomAccessReadBuffer(inputStream).use {
        TEXT_STRIPPER.getText(PDFParser(it).parse())
    }

    private fun File.asStream(): ByteArrayInputStream {
        val pdf = PdfDocument()
        pdf.loadFromFile(absolutePath)
        val bos = ByteArrayOutputStream()
        pdf.saveToStream(bos, com.spire.pdf.FileFormat.PDF)
        return ByteArrayInputStream(bos.toByteArray())
    }



}