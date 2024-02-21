package com.ingenifi.unifile

import com.google.common.io.Resources.getResource
import com.google.common.io.Resources.toString
import java.io.File
import java.nio.charset.Charset.defaultCharset
import java.nio.file.Files


object Testing {

    fun resourceAsFile(extension: String, name: String = "simple") = File("src/test/resources/${name}.${extension}")
    fun simplePaths(vararg extensions: String) = extensions.map { "src/test/resources/simple.$it" }
    fun resourceAsString(resource: String): String = toString(getResource(resource), defaultCharset())

    private val separatorLine = "-----------------"
    fun outputResource(resource: String) {
        println("\n$separatorLine\n$resource")
        println(resourceAsString(resource))
        println(separatorLine)
    }

    fun Any?.output(heading: String? = null) {

        this?.also {
            println("\n$separatorLine")
            heading?.also { println(it) }
            println(toString())
            println(separatorLine)
        }
    }

    fun vpnOn(): Boolean {
        val httpProxyVariable = System.getenv("HTTP_PROXY")
        println("httpProxyVariable = $httpProxyVariable")
        return httpProxyVariable != null
    }

    fun tempFile(name: String = "testFile", extension: String = "txt", content: String = ""): File {
        // Create a temporary directory to hold the file, ensuring it gets deleted on exit
        val tempDir = Files.createTempDirectory(null).toFile().apply { deleteOnExit() }

        // Construct the complete file name with extension
        val fileName = "$name.$extension"

        // Create the file within the temporary directory
        val file = File(tempDir, fileName).apply {
            writeText(content)
            deleteOnExit()
        }

        return file
    }
}