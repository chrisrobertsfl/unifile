package com.ingenifi.unifile.formatter.jira

import com.ingenifi.unifile.KeywordExtractor
import com.ingenifi.unifile.ParameterStore
import com.ingenifi.unifile.UnsecuredHttpClient
import com.ingenifi.unifile.Verbosity
import com.ingenifi.unifile.formatter.toc.TableOfContents
import io.kotest.core.annotation.Ignored
import io.kotest.core.spec.style.StringSpec
import java.io.File


@Ignored
class JiraFormatterSpec : StringSpec({

    val keywordExtractor = KeywordExtractor(filters = listOf({ it.length in 3..24 }, { !it.startsWith("4") }, { s -> s.firstOrNull()?.isDigit() == false }))
    val client = UnsecuredHttpClient.create()

    "story" {
        val formatter = JiraFormatter(
            parameterStore = ParameterStore.NONE,
            file = File("src/test/resources/simple.jira"),
            client = client,
            keywordExtractor =  keywordExtractor,
            verbosity = Verbosity.NONE,
            toc = TableOfContents()
        )
        val text = formatter.format(1)
        println("formatter.lastNumber() = ${formatter.lastNumber()}")
        println(text)
    }
})


