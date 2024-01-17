package com.ingenifi.unifile.content.formatter

import com.ingenifi.unifile.PowerPointConverter
import java.io.File

data class PowerPointSource(val file: File) : Source {
    override fun description(): String = file.toText()
    override fun title(): String = file.name
    private fun File.toText(): String = PowerPointConverter().convert(this)

}