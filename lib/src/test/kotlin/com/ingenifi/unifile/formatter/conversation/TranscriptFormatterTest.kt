package com.ingenifi.unifile.formatter.conversation

import com.ingenifi.unifile.model.generators.KeywordExtractor
import com.ingenifi.unifile.formatter.toc.TableOfContents
import io.kotest.core.annotation.Ignored
import io.kotest.core.spec.style.FunSpec
import java.io.File

@Ignored
class TranscriptFormatterTest : FunSpec({
    test("should correctly parse and format the conversation document") {
        // Assuming a mock file `conversation-document.tmpl` exists in the test resources directory
        val file = File("src/test/resources/simple.transcript")
        val formatter = TranscriptFormatter(file, KeywordExtractor(), TableOfContents())

        val formattedText = formatter.format(1)
        println(formattedText)
    }
})


