package com.ingenifi.unifile.formatter

import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfReader
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor
import java.io.File

class PdfConverter {

    fun convert(pdf: File): String {
        val reader = PdfReader(pdf.absolutePath)
        val pdfDoc = PdfDocument(reader)
        val stringBuilder = StringBuilder()

        for (i in 1..pdfDoc.numberOfPages) {
            val page = pdfDoc.getPage(i)
            stringBuilder.append(PdfTextExtractor.getTextFromPage(page)).append("\n")
        }

        pdfDoc.close()
        return stringBuilder.toString()
    }
}
