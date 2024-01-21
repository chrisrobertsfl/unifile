package com.ingenifi.unifile.formatter.json

import com.ingenifi.unifile.formatter.FileSource
import java.io.File

data class JsonSource(override val file: File) : FileSource {
    override fun description(): String = JsonConverter().convert(file.readText())
}