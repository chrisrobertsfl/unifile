package com.ingenifi.unifile.formatter.powerpoint

import com.ingenifi.unifile.formatter.pdf.PdfConverter
import java.io.File

class PowerPointConverter {

    fun convert(powerPointFile: File): String {
        val pdf = convertToPdf(powerPointFile)
        return PdfConverter().convert(pdf)
    }

    private fun convertToPdf(pptxFile: File): File {
        val pdfFileName = pptxFile.absolutePath.replaceAfterLast('.', "pdf")
        val processBuilder = ProcessBuilder("libreoffice", "--headless", "--convert-to", "pdf", pptxFile.absolutePath)
        val process = processBuilder.start()
        val exitCode = process.waitFor()

        if (exitCode != 0) {
            throw RuntimeException("LibreOffice conversion failed with exit code $exitCode")
        }

        return File(pdfFileName)
    }
}