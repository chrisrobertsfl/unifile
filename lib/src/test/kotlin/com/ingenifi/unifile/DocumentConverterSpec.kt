package com.ingenifi.unifile

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.types.shouldBeTypeOf
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.extension

class DocumentConverterSpec : StringSpec({
    "convert docx" {
        val path = Paths.get("src/test/resources/simple.doc")
        DocumentConverter.from(path.extension).convert(path).shouldBeTypeOf<DocxOutput>()
    }
})

sealed interface DocumentConverter {

    companion object {
        fun from(extension: String): DocumentConverter {
            return DocxConverter
        }
    }

    object DocxConverter : DocumentConverter {
        override fun convert(path: Path): Output = DocxOutput(path)
    }

    fun convert(path : Path) : Output
}

