package com.ingenifi.unifile.conversion.formatter

import com.ingenifi.unifile.content.KeywordExtractor
import com.ingenifi.unifile.content.formatter.DocumentFormatter
import io.kotest.core.annotation.Ignored
import io.kotest.core.spec.style.StringSpec
import org.w3c.dom.Element
import org.w3c.dom.Node
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory


@Ignored
class DocumentFormatterSpec : StringSpec({

    val keywordExtractor = KeywordExtractor(filters = listOf({ it.length in 3..24 }, { !it.startsWith("4") }, { s -> s.firstOrNull()?.isDigit() == false }))
    val plainTextFormatter = PlainTextFormatter(file = File("src/test/resources/simple.txt"), keywordExtractor = keywordExtractor)
    val xmlFormatter = XmlFormatter(file = File("src/test/resources/simple.xml"), keywordExtractor = keywordExtractor)

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

data class PlainTextFormatter(private val file: File, private val keywordExtractor: KeywordExtractor) : DocumentFormatter {
    private val delegate = FormatterDelegate(file, keywordExtractor)
    override fun format(number: Int): String = delegate.format(number, file.readText(), "plain-document.tmpl")
    override fun lastNumber(): Int = delegate.lastNumber()
}

class XmlFormatter(private val file: File, private val keywordExtractor: KeywordExtractor) : DocumentFormatter {
    private val delegate = FormatterDelegate(file, keywordExtractor)

    override fun format(number: Int): String = delegate.format(number, getBodyFromXml(), "xml-document.tmpl")

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

class FormatterDelegate(private val file: File, private val keywordExtractor: KeywordExtractor) {
    private var lastNumber = 0

    companion object {
        private val templateCache = mutableMapOf<String, String>()

        private fun loadTemplate(templatePath: String): String {
            return templateCache.getOrPut(templatePath) {
                FormatterDelegate::class.java.classLoader.getResource(templatePath)?.readText() ?: throw IllegalArgumentException("Template resource could not be read: $templatePath")
            }
        }
    }

    fun format(number: Int, body: String, templatePath: String): String {
        val keywords = mutableSetOf<String>()
        keywords.addAll(keywordExtractor.extract(body))
        keywords.addAll(file.keywords())
        val template = loadTemplate(templatePath)
        val text = template.replace("{number}", number.toString()).replace("{filename}", file.name).replace("{description}", body)
            .replace("{keywords}", keywords.joinToString(", "))
        lastNumber = number + 1
        return text
    }

    fun lastNumber(): Int = lastNumber

    private fun File.keywords(): List<String> {
        val fileNameWithoutExtension = name.substringBeforeLast(".")
        val keywordsFromName = fileNameWithoutExtension.split(Regex("\\W+")).filter { it.isNotBlank() }
        return (keywordsFromName + extension).distinct()
    }
}

