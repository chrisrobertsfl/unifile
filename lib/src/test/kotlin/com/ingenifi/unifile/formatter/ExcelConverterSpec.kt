package com.ingenifi.unifile.formatter

import com.ingenifi.unifile.formatter.excel.ExcelConverter
import io.kotest.assertions.asClue
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import java.io.File

class ExcelConverterSpec : StringSpec({

    "convert recipe" {

        ExcelConverter().convert(File("src/test/resources/recipes.xlsx")).asClue {
            it.size shouldBe 2
            it.keys shouldBe setOf("Ingredients", "Instructions")
            it["Ingredients"]?.contains("ribeye") shouldBe true
            it["Instructions"]?.contains("oven") shouldBe true
        }
    }
}) {
}