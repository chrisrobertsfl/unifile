package com.ingenifi.unifile

import com.google.common.io.Resources.getResource
import com.google.common.io.Resources.toString
import java.io.File
import java.nio.charset.Charset.defaultCharset


fun simpleFile(extension : String) = File("src/test/resources/simple.${extension}")
fun simplePaths(vararg extensions: String) = extensions.map { "src/test/resources/simple.$it" }
fun resourceAsString(resource: String): String = toString(getResource(resource), defaultCharset())

fun outputResource(resource : String) {
    println("\n-----------------\n$resource")
    println(resourceAsString(resource))
    println("----------------")
}

fun String.output(heading : String?) {
    println("\n-----------------")
    heading?.let { println(it) }
    println(this)
    println("----------------")
}

fun vpnOn() : Boolean {
    val httpProxyVariable = System.getenv("HTTP_PROXY")
    println("httpProxyVariable = ${httpProxyVariable}")
    return httpProxyVariable != null
}