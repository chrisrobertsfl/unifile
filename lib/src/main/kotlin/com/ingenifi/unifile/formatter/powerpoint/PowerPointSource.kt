package com.ingenifi.unifile.formatter.powerpoint

import com.ingenifi.unifile.formatter.FileSource
import java.io.File

data class PowerPointSource(override val file: File) : FileSource {
    override fun description(): String = file.toText()
    override fun title(): String = file.name
    private fun File.toText(): String = PowerPointConverter().convert(this)

}