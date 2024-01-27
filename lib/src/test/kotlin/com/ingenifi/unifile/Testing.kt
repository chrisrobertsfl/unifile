package com.ingenifi.unifile

import com.google.common.io.Resources.getResource
import com.google.common.io.Resources.toString
import java.nio.charset.Charset.defaultCharset

fun simplePaths(vararg extensions: String) = extensions.map { "src/test/resources/simple.$it" }
fun resourceAsString(resource: String): String = toString(getResource(resource), defaultCharset())