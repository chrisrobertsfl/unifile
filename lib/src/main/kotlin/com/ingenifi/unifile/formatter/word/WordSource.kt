package com.ingenifi.unifile.formatter.word

import com.ingenifi.unifile.formatter.FileSource
import com.ingenifi.unifile.model.document.WordConverter
import java.io.File

data class WordSource(override val file: File) : FileSource {
    override fun description(): String = file.toText()
    private fun File.toText(): String = WordConverter().convert(this)

}