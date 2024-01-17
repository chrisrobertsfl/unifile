package com.ingenifi.unifile.content.conversion

import com.ingenifi.unifile.content.JsonContent
import java.io.File

object Unknown : ContentConverter {
    override fun convert(file: File): List<JsonContent> = throw UnsupportedOperationException("No converter associate with path '${file.absolutePath}'")

}