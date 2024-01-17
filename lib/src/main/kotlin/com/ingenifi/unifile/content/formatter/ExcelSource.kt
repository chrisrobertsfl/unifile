package com.ingenifi.unifile.content.formatter

import com.ingenifi.unifile.ExcelConverter
import java.io.File

data class ExcelSource(val file: File) : Source {
    override fun description(): String = file.toText()
    override fun title(): String = file.name
    private fun File.toText(): String = ExcelConverter().convert(this)

}