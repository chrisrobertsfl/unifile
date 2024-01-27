package com.ingenifi.unifile.model.document

import com.ingenifi.unifile.model.generators.pdf.PdfConverter
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import java.io.File

class PdfConverterTest : StringSpec({

    "convert" {
       PdfConverter().convert(File("src/test/resources/simple.pdf")) shouldBe "Hello PDF"
    }
})
