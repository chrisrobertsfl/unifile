package com.ingenifi.unifile.model.generators.word

import org.apache.poi.hwpf.HWPFDocument
import org.apache.poi.hwpf.extractor.WordExtractor
import org.apache.poi.xwpf.usermodel.XWPFDocument
import java.io.File
import java.io.FileInputStream

class WordConverter {

    fun convert(wordFile: File): String {
        return if (wordFile.extension == "doc") {
            convertDoc(wordFile)
        } else {
            convertDocx(wordFile)
        }
    }

    private fun convertDoc(file: File): String {
        FileInputStream(file).use { fis ->
            val document = HWPFDocument(fis)
            val extractor = WordExtractor(document)
            val text = extractor.text
            extractor.close()
            return text
        }
    }

    private fun convertDocx(file: File): String {
        FileInputStream(file).use { fis ->
            val document = XWPFDocument(fis)
            return document.paragraphs.joinToString("\n") { it.text }
        }
    }
}
