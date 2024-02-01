package com.ingenifi.unifile

import com.ingenifi.unifile.Testing.output
import com.ingenifi.unifile.Testing.resourceAsString
import com.ingenifi.unifile.Testing.simplePaths
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class UniFileRunnerSpecification : StringSpec({
    "run" {
        val input = InputPaths(simplePaths("txt", "xml", "pdf", "json", "csv", "docx", "html", "transcript"))
        val output = OutputPath.StringOutputPath()
        UniFileRunner(input = input, verbosity = Verbosity(verbose = true, level = 0)).combineFiles(output)
        val expectedDocument = resourceAsString("expected-document.txt")
        output.contents.output()
        output.contents shouldBe expectedDocument
    }
})


