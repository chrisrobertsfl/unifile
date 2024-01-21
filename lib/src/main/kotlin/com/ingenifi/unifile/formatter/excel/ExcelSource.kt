package com.ingenifi.unifile.formatter.excel

import com.ingenifi.unifile.formatter.FileSource
import java.io.File

data class ExcelSource(override val file: File) : FileSource {
    override fun description(): String = file.toText()
    private fun File.toText(): String = ExcelConverter().convert(this)

}