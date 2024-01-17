package com.ingenifi.unifile.formatter.excel

import com.ingenifi.unifile.formatter.Source
import java.io.File

data class ExcelSource(val file: File) : Source {
    override fun description(): String = file.toText()
    override fun title(): String = file.name
    private fun File.toText(): String = ExcelConverter().convert(this)

}