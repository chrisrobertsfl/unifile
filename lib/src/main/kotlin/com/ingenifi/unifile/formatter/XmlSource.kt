package com.ingenifi.unifile.formatter

import org.w3c.dom.Element
import org.w3c.dom.Node
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory

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