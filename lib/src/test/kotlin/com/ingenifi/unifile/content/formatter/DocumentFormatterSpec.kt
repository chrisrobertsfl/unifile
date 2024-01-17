package com.ingenifi.unifile.content.formatter

import com.ingenifi.unifile.*
import com.ingenifi.unifile.content.KeywordExtractor
import io.kotest.core.annotation.Ignored
import io.kotest.core.spec.style.StringSpec
import io.ktor.client.request.*
import io.ktor.client.statement.*
import java.io.File
import java.util.*


@Ignored

class DocumentFormatterSpec : StringSpec({

    val keywordExtractor = KeywordExtractor(filters = listOf({ it.length in 3..24 }, { !it.startsWith("4") }, { s -> s.firstOrNull()?.isDigit() == false }))
    val plainTextFormatter = PlainTextFormatter(file = File("src/test/resources/simple.txt"), keywordExtractor = keywordExtractor)
    val xmlFormatter = XmlFormatter(file = File("src/test/resources/simple.xml"), keywordExtractor = keywordExtractor)

    val client = UnsecuredHttpClient.create()
    val confluenceApi = ConfluenceApi(client, "TKMA5QX", "Kotlin2023!!")

    suspend fun httpGet(request: String): String {
        val user = "TKMA5QX"
        val password = "Kotlin2023!!"
        val encodedCredentials = Base64.getEncoder().encodeToString("$user:$password".toByteArray())
        return client.get(request) {
            header("Authorization", "Basic $encodedCredentials")
        }.bodyAsText()
    }

    "plain text" {
        val text = plainTextFormatter.format(1)
        val lastNumber = plainTextFormatter.lastNumber()
        println("lastNumber = ${lastNumber}")
        println(text)
    }

    "xml" {
        val text = xmlFormatter.format(1)
        val lastNumber = xmlFormatter.lastNumber()
        println("lastNumber = ${lastNumber}")
        println(text)
    }

    "json" {
        val formatter = JsonFormatter(file = File("src/test/resources/simple.json"), keywordExtractor = keywordExtractor)
        val text = formatter.format(1)
        val lastNumber = formatter.lastNumber()
        println("lastNumber = ${lastNumber}")
        println(text)
    }

    "pdf" {
        val formatter = PdfFormatter(file = File("src/test/resources/simple.pdf"), keywordExtractor = keywordExtractor)
        val text = formatter.format(1)
        val lastNumber = formatter.lastNumber()
        println("lastNumber = ${lastNumber}")
        println(text)
    }

    "confluence" {
        val linkString = "https://confluence.kohls.com:8443/display/OE/Software+Quality+and+Performance"
        val link = ConfluenceLink.create(linkString, confluenceApi)
        val formatter = ConfluencePageFormatter(link,keywordExtractor)
        val text = formatter.format(1)
        val lastNumber = formatter.lastNumber()
        println("lastNumber = ${lastNumber}")
        println(text)
    }

    "confluence pages" {
        val formatter = ConfluencePagesFormatter(client = client, file = File("src/test/resources/simple.clink"), keywordExtractor = keywordExtractor)
        val text = formatter.format(1)
        val lastNumber = formatter.lastNumber()
        println("lastNumber = ${lastNumber}")
        println(text)
    }

    "do all" {
        var number = 1
        var string = ""
        val files = listOf("simple.json", "simple.txt", "flowers_text_50_words.txt").map {
            File("src/test/resources/$it")
        }
        for (file in files) {
            val formatter = PlainTextFormatter(file = file, keywordExtractor = keywordExtractor)
            string += formatter.format(number)
            number = formatter.lastNumber()
        }

        println("lastNumber = ${number}")
        println(string)
    }

})


