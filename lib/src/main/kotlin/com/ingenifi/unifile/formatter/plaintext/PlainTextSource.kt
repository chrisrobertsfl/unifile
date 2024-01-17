package com.ingenifi.unifile.formatter.plaintext

import com.ingenifi.unifile.formatter.Source
import java.io.File

class PlainTextSource(private val file: File): Source {
    override fun description(): String = file.readText()
    override fun title(): String = file.name
}