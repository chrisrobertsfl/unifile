package com.ingenifi.unifile.formatter.pdf

import com.ingenifi.unifile.formatter.FileSource
import java.io.File

data class PdfSource(override val file: File) : FileSource {
    override fun description(): String = file.toText()
    private fun File.toText(): String = PdfConverter().convert(this)

}