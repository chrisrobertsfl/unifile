package com.ingenifi.unifile.model.generators.kotlin

import com.ingenifi.unifile.Testing.output
import com.ingenifi.unifile.model.document.Document
import com.ingenifi.unifile.model.generators.SectionGeneratorConfig
import com.ingenifi.unifile.model.generators.document.DocumentGenerator
import io.kotest.core.spec.style.StringSpec
import java.io.File

class KotlinGeneratorSpecification : StringSpec({

    val file = File("/Users/TKMA5QX/projects/unifile/lib/src/main/kotlin/com/ingenifi/unifile/model/generators/jira/EpicChild.kt")
    "generate heading name" {
        val heading = KotlinGenerator.headingName(file)
        heading.output()
    }


    "generate" {
        val config = SectionGeneratorConfig()
        val generator = KotlinGenerator(config = config, number = 1, file = file)
        val sections = generator.generate()

        DocumentGenerator(Document(sections)).generate().output()
    }
})