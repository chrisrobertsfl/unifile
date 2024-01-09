package com.ingenifi.unifile

import java.nio.file.Path
import kotlin.io.path.absolutePathString

sealed interface DocumentConverter {

    companion object {
        fun from(extension: String): DocumentConverter = when(extension.lowercase()) {
            "docx" -> DocxConverter
            "doc" -> DocxConverter
            else -> UnknownConverter
        }
    }

    object DocxConverter : DocumentConverter {
        override fun convert(path: Path): Output = DocxOutput(path)
    }

    object UnknownConverter : DocumentConverter {
        override fun convert(path: Path): Output = throw UnsupportedOperationException("No converter associate with path '${path.absolutePathString()}'")
    }

    fun convert(path : Path) : Output
}