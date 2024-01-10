package com.ingenifi.unifile

import java.nio.file.Path
import kotlin.io.path.absolutePathString

sealed interface DocumentConverter {

    companion object {
        fun from(extension: String): DocumentConverter = when(extension.lowercase()) {
            "doc" -> DocxConverter
            "docx" -> DocxConverter
            "pdf" -> PdfConverter
            "ppt" -> PptxConverter
            "pptx" -> PptxConverter
            "txt" -> TextConverter
            "xml" -> TextConverter
            else -> UnknownConverter
        }
    }

    object DocxConverter : DocumentConverter {
        override fun convert(path: Path): Output = DocxOutput(path)
    }
object PdfConverter : DocumentConverter {
        override fun convert(path: Path): Output = PdfOutput(path)
    }

    object PptxConverter : DocumentConverter {
        override fun convert(path: Path): Output = PptxOutput(path)
    }

    object TextConverter : DocumentConverter {
        override fun convert(path: Path): Output = PlainTextOutput(path)
    }

    object UnknownConverter : DocumentConverter {
        override fun convert(path: Path): Output = throw UnsupportedOperationException("No converter associate with path '${path.absolutePathString()}'")
    }

    fun convert(path : Path) : Output
}