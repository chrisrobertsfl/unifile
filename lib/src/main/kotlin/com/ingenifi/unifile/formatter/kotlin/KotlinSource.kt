package com.ingenifi.unifile.formatter.kotlin

import com.ingenifi.unifile.formatter.FileSource
import java.io.File

class KotlinSource(override val file: File) : FileSource {
    override fun description(): String = file.readText()
}