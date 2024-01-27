package com.ingenifi.unifile.formatter.powerpoint

import com.ingenifi.unifile.formatter.FileSource
import com.ingenifi.unifile.model.generators.powerpoint.PowerPointConverter
import java.io.File

data class PowerPointSource(override val file: File) : FileSource {
    override fun description(): String = file.toText()
    private fun File.toText(): String = PowerPointConverter().convert(this)

}