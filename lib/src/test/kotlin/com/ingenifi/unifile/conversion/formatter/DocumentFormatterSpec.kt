package com.ingenifi.unifile.conversion.formatter

import com.ingenifi.unifile.*
import com.ingenifi.unifile.content.KeywordExtractor
import com.ingenifi.unifile.content.formatter.DocumentFormatter
import io.kotest.core.annotation.Ignored
import io.kotest.core.spec.style.StringSpec
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import jdk.jfr.Percentage
import kotlinx.coroutines.runBlocking
import org.w3c.dom.Element
import org.w3c.dom.Node
import java.io.File
import java.util.*
import javax.xml.parsers.DocumentBuilderFactory


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

data class ConfluenceLink(val link: String, val api: ConfluenceApi, val pageId: String, val result : FetchResult)  : Source {

    override fun description() = result.get("body") as String
    override fun title() = result.get("title") as String
    companion object {
        suspend fun create(link: String, api: ConfluenceApi): ConfluenceLink {
            val pageId = getPageId(link, api)
            val result = getResult(pageId, api)
            return ConfluenceLink(link, api, pageId, result)
        }
        private suspend fun getPageId(link: String, api: ConfluenceApi): String = api.fetchPageId(link) ?: throw IllegalArgumentException("no page id")

        private suspend fun getResult(pageId : String, api : ConfluenceApi) = api.fetch(pageId, FetchOption("title", "$.title"), FetchOption("body", "$.body.view.value", true))
    }
}

data class ConfluencePageFormatter(private val link : ConfluenceLink, private val keywordExtractor: KeywordExtractor) : DocumentFormatter {
    private val delegate = Delegate(link, keywordExtractor)
    override fun format(number: Int): String  = delegate.format(number,  "confluence-document.tmpl",  extractPercentage = 0.1)
    override fun lastNumber(): Int = delegate.lastNumber()
}

data class ConfluencePagesFormatter(private val client: HttpClient, private val file : File, private val keywordExtractor: KeywordExtractor) : DocumentFormatter {
    private val api = ConfluenceApi(client, "TKMA5QX", "Kotlin2023!!")
    private var lastNumber = 0

    override fun format(number: Int): String = runBlocking { formatSuspended(number) }
    suspend fun formatSuspended(number: Int): String {
        lastNumber = number
        return file.readLines().map{ ConfluenceLink.create(it, api)}
            .map { ConfluencePageFormatter(it, keywordExtractor).format(lastNumber++) }
            .joinToString("\n")
    }
    override fun lastNumber(): Int = lastNumber
}

data class PlainTextFormatter(private val file: File, private val keywordExtractor: KeywordExtractor) : DocumentFormatter {
    private val delegate = Delegate(FileSource(file), keywordExtractor)
    override fun format(number: Int): String = delegate.format(number,  "plain-document.tmpl")
    override fun lastNumber(): Int = delegate.lastNumber()
}

data class XmlFormatter(private val file: File, private val keywordExtractor: KeywordExtractor) : DocumentFormatter {
    private val delegate = Delegate(XmlSource(file), keywordExtractor)

    override fun format(number: Int): String = delegate.format(number, "xml-document.tmpl")

    override fun lastNumber(): Int = delegate.lastNumber()
    private fun getBodyFromXml(): String {
        val documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
        val document = documentBuilder.parse(file)
        val root = document.documentElement
        return buildString {
            appendXmlContent(root, 0, this)
        }
    }

    private fun appendXmlContent(node: Node, depth: Int, builder: StringBuilder) {
        val indent = "  ".repeat(depth)

        if (node.nodeType == Node.ELEMENT_NODE && !isNodeEmpty(node)) {
            val attributes = getNodeAttributes(node)
            builder.appendLine("$indent${node.nodeName}${attributes}:")
            val childNodes = node.childNodes
            for (i in 0 until childNodes.length) {
                appendXmlContent(childNodes.item(i), depth + 1, builder)
            }
        } else if (node.nodeType == Node.TEXT_NODE) {
            val textContent = node.textContent.trim()
            if (textContent.isNotEmpty()) {
                builder.appendLine("$indent$textContent")
            }
        }
    }

    private fun getNodeAttributes(node: Node): String {
        if (node.nodeType != Node.ELEMENT_NODE) return ""

        val element = node as Element
        val attributes = element.attributes
        return (0 until attributes.length).asSequence().map { attributes.item(it) }.filter { it.nodeValue.trim().isNotEmpty() }
            .joinToString(separator = ", ", prefix = " [", postfix = "]") { "${it.nodeName}=${it.nodeValue}" }
    }

    private fun isNodeEmpty(node: Node): Boolean {
        if (node.nodeType == Node.ELEMENT_NODE) {
            val hasNonEmptyText = node.textContent.trim().isNotEmpty()
            val hasChildren = (0 until node.childNodes.length).any {
                !isNodeEmpty(node.childNodes.item(it))
            }
            return !hasNonEmptyText && !hasChildren
        }
        return false
    }
}



interface Source {
    fun description() : String
    fun title() : String

}

class FileSource(private val file: File): Source {
    override fun description(): String = file.readText()
    override fun title(): String = file.name
}

class XmlSource(private val file : File) : Source {
    override fun description(): String {
        val documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
        val document = documentBuilder.parse(file)
        val root = document.documentElement
        return buildString { appendXmlContent(root, 0, this) }
    }

    private fun appendXmlContent(node: Node, depth: Int, builder: StringBuilder) {
        val indent = "  ".repeat(depth)

        if (node.nodeType == Node.ELEMENT_NODE && !isNodeEmpty(node)) {
            val attributes = getNodeAttributes(node)
            builder.appendLine("$indent${node.nodeName}${attributes}:")
            val childNodes = node.childNodes
            for (i in 0 until childNodes.length) {
                appendXmlContent(childNodes.item(i), depth + 1, builder)
            }
        } else if (node.nodeType == Node.TEXT_NODE) {
            val textContent = node.textContent.trim()
            if (textContent.isNotEmpty()) {
                builder.appendLine("$indent$textContent")
            }
        }
    }

    private fun getNodeAttributes(node: Node): String {
        if (node.nodeType != Node.ELEMENT_NODE) return ""

        val element = node as Element
        val attributes = element.attributes
        return (0 until attributes.length).asSequence().map { attributes.item(it) }.filter { it.nodeValue.trim().isNotEmpty() }
            .joinToString(separator = ", ", prefix = " [", postfix = "]") { "${it.nodeName}=${it.nodeValue}" }
    }

    private fun isNodeEmpty(node: Node): Boolean {
        if (node.nodeType == Node.ELEMENT_NODE) {
            val hasNonEmptyText = node.textContent.trim().isNotEmpty()
            val hasChildren = (0 until node.childNodes.length).any {
                !isNodeEmpty(node.childNodes.item(it))
            }
            return !hasNonEmptyText && !hasChildren
        }
        return false
    }

    override fun title(): String = file.name

}
class Delegate(private val source: Source, private val keywordExtractor: KeywordExtractor) {
    private var lastNumber = 0

    companion object {
        private val templateCache = mutableMapOf<String, String>()

        private fun loadTemplate(templatePath: String): String {
            return templateCache.getOrPut(templatePath) {
                Delegate::class.java.classLoader.getResource(templatePath)?.readText()
                    ?: throw IllegalArgumentException("Template resource could not be read: $templatePath")
            }
        }
    }

    fun format(number: Int, templatePath: String, replacements: Map<String, String> = mapOf(), extractPercentage: Double = 0.025): String {
        val description = source.description()
        val title = source.title()
        val keywords = mutableSetOf<String>()
        keywords.addAll(keywordExtractor.extract(text = description, percentage = extractPercentage))

        var template = loadTemplate(templatePath)

        // Standard and dynamic replacements
        template = template.replace("{number}", number.toString())
            .replace("{title}", title)
            .replace("{description}", description)
            .replace("{keywords}", keywords.joinToString(", "))
        replacements.forEach { (key, value) -> template = template.replace("{$key}", value) }

        lastNumber = number + 1
        return template
    }

    fun lastNumber(): Int = lastNumber
}


