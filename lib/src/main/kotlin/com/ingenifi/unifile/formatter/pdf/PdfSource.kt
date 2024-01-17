package com.ingenifi.unifile.formatter.pdf

import com.ingenifi.unifile.formatter.Source
import java.io.File

data class PdfSource(val file: File) : Source {
    override fun description(): String = file.toText()
    override fun title(): String = file.name
    private fun File.toText(): String = PdfConverter().convert(this)

}