package com.ingenifi.unifile.content.formatter

import java.io.File

class FileSource(private val file: File): Source {
    override fun description(): String = file.readText()
    override fun title(): String = file.name
}