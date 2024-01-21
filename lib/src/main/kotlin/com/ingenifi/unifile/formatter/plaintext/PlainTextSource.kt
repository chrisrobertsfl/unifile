package com.ingenifi.unifile.formatter.plaintext

import com.ingenifi.unifile.formatter.FileSource
import java.io.File

class PlainTextSource(override val file: File) : FileSource {
    override fun description(): String = file.readText()
}