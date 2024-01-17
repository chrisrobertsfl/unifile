package com.ingenifi.unifile.formatter.json

import com.ingenifi.unifile.formatter.Source
import java.io.File

data class JsonSource(val file : File) : Source {
    override fun description(): String = JsonConverter().convert(file.readText())

    override fun title(): String = file.name

}