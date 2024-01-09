package com.ingenifi.unifile

import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import java.nio.file.Paths

class UnifyingMergerSpec : FeatureSpec({

    val unifile = UnifyingMerger()
    val textContents = """
            Hello TEXT
            
        """.trimIndent()
    val pdfContents = """
            Hello PDF
            
        """.trimIndent()
    val textOutput = PlainTextOutput(path = Paths.get("src/test/resources/simple.txt"))
    val pdfOutput = PdfOutput(path = Paths.get("src/test/resources/simple.pdf"))
    val docxOutput = DocxOutput(path = Paths.get("src/test/resources/simple.pdf"))

    feature("Text files") {

        scenario("merge one file") {
            unifile.merge(textOutput) shouldBe textContents
        }

        scenario("merge two files") {
            unifile.merge(textOutput, textOutput) shouldBe """
            $textContents
            ---
            $textContents
        """.trimIndent()
        }
    }

    feature("PDF Files") {
        scenario("merge one file") {
            unifile.merge(pdfOutput) shouldBe pdfContents
        }

        scenario("merge two files") {
            unifile.merge(pdfOutput, pdfOutput) shouldBe """
            $pdfContents
            ---
            $pdfContents
        """.trimIndent()
        }
    }


    feature("DOCX Files") {
        scenario("merge one file") {
            unifile.merge(pdfOutput) shouldBe pdfContents
        }

        scenario("merge two files") {
            unifile.merge(pdfOutput, pdfOutput) shouldBe """
            $pdfContents
            ---
            $pdfContents
        """.trimIndent()
        }
    }
    feature("All types of files") {

        scenario("Text and PDF") {

            unifile.merge(textOutput, pdfOutput) shouldBe """
            $textContents
            ---
            $pdfContents
        """.trimIndent()
        }
    }
})