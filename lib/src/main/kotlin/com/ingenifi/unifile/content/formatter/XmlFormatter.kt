package com.ingenifi.unifile.content.formatter

import com.ingenifi.unifile.content.KeywordExtractor
import org.w3c.dom.Element
import org.w3c.dom.Node
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory

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