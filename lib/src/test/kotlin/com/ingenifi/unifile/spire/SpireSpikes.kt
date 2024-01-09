package com.ingenifi.unifile.spire

import com.google.common.io.Resources
import com.spire.pdf.PdfDocument
import io.kotest.core.annotation.Ignored
import io.kotest.core.spec.style.StringSpec
import org.apache.pdfbox.Loader
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.text.PDFTextStripper
import java.io.File
import java.io.FileOutputStream

import java.io.OutputStream


@Ignored
class SpireSpikes : StringSpec({
    "Convert PDF to text" {
        val inputStream = Resources.getResource("why-kotlin.pdf").openStream()
        val pdf = PdfDocument.mergeFiles(arrayOf(inputStream))
        val outputStream: OutputStream = FileOutputStream("/tmp/combined.pdf")
        pdf.save(outputStream)
        pdf.close()
        Loader.loadPDF(File("/tmp/combined.pdf")).use{
            println(PDFTextStripper().getText(it))
        }
    }

    "Convert Text to PDF" {
        val inputStream = Resources.getResource("flowers_text_50_words.txt").openStream()

        val pdf = PdfDocument.mergeFiles(arrayOf(inputStream))
        val outputStream: OutputStream = FileOutputStream("/tmp/text.pdf")
        pdf.save(outputStream)
        pdf.close()
        Loader.loadPDF(File("/tmp/text.pdf")).use{
            println(PDFTextStripper().getText(it))
        }
    }
})




