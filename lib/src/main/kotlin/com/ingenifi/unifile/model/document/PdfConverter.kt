package com.ingenifi.unifile.model.document

import org.apache.pdfbox.pdmodel.PDDocument.load
import org.apache.pdfbox.text.PDFTextStripper
import java.io.File

class PdfConverter {
    fun convert(pdf: File): String = load(pdf).use { PDFTextStripper().getText(it).trim() }
}