package com.ingenifi.unifile

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.types.shouldBeTypeOf
import java.nio.file.Paths
import kotlin.io.path.extension

class DocumentConverterSpec : StringSpec({
    "convert docx" {
        val path = Paths.get("src/test/resources/simple.doc")
        DocumentConverter.from(path.extension).convert(path).shouldBeTypeOf<DocxOutput>()
    }

    "convert unknown" {
        shouldThrow<UnsupportedOperationException> { DocumentConverter.from("unknown").convert(Paths.get("hello.unknown")) }

    }
})

