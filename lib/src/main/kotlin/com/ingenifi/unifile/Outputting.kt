package com.ingenifi.unifile

import com.spire.doc.Document
import com.spire.doc.FileFormat
import com.spire.presentation.Presentation
import org.apache.pdfbox.io.RandomAccessReadBuffer
import org.apache.pdfbox.pdfparser.PDFParser
import org.apache.pdfbox.text.PDFTextStripper
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.nio.file.Path
import kotlin.io.path.absolutePathString
import kotlin.io.path.inputStream
import kotlin.io.path.readText


sealed interface Output {
    object NoopOutput : Output
}

interface TextOutput : Output {
    val fullPath: String
    fun asText(): String
}

data class PlainTextOutput(private val path: Path) : TextOutput {
    override val fullPath: String
        get() = path.absolutePathString()

    override fun asText(): String = path.readText()
}

data class PdfOutput(private val path: Path) : TextOutput {
    override val fullPath: String
        get() = path.absolutePathString()

    override fun asText(): String = RandomAccessReadBuffer(path.inputStream()).use {
        TEXT_STRIPPER.getText(PDFParser(it).parse())
    }

    companion object {
        val TEXT_STRIPPER = PDFTextStripper()
    }
}

sealed interface PdfStreamOutput : Output {
    fun asStream(): ByteArrayInputStream
}

data class DocxOutput(private val path: Path) : PdfStreamOutput {
    override fun asStream(): ByteArrayInputStream {
        val document = Document()
        document.loadFromFile(path.absolutePathString())
        val bos = ByteArrayOutputStream()
        document.saveToStream(bos, FileFormat.PDF)
        return ByteArrayInputStream(bos.toByteArray())
    }
}

data class PptxOutput(private val path: Path) : PdfStreamOutput {
    override fun asStream(): ByteArrayInputStream {
        val presentation = Presentation()
        presentation.loadFromFile(path.absolutePathString())
        val bos = ByteArrayOutputStream()
        try {
            presentation.saveToFile(bos, com.spire.presentation.FileFormat.PDF)
        } catch( e : Exception) {
            println("Finally caught npe: ${e.message}")
        }
        return ByteArrayInputStream(bos.toByteArray())
    }
}
