package com.ingenifi.unifile

import com.google.common.io.Resources.getResource
import com.google.common.io.Resources.toString
import java.io.File
import java.nio.charset.Charset.defaultCharset


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

}