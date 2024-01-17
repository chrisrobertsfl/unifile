package com.ingenifi.unifile.content.formatter

import com.ingenifi.unifile.JsonConverter
import java.io.File

data class JsonSource(val file : File) : Source {
    override fun description(): String = JsonConverter().convert(file.readText())

    override fun title(): String = file.name

}