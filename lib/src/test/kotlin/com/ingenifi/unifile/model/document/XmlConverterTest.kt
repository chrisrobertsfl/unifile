package com.ingenifi.unifile.model.document

import com.ingenifi.unifile.model.generators.xml.XmlConverter
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import java.io.File

class XmlConverterTest : StringSpec({

    "convert" {
       XmlConverter().convert(File("src/test/resources/simple.xml")) shouldBe """
           tag []:
             Hello XML
       """.trimIndent()
    }
})
