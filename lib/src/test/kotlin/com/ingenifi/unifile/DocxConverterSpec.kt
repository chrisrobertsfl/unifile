package com.ingenifi.unifile

import com.spire.doc.Document
import io.kotest.core.spec.style.StringSpec
import java.nio.file.Paths
import kotlin.io.path.absolutePathString

class DocxConverterSpec : StringSpec({
    "convert" {
        Document().loadFromFile(Paths.get("src/test/resources/simple.doc").absolutePathString())
    }
})