package com.ingenifi.unifile.content.conversion

import com.ingenifi.unifile.content.Content
import java.io.File

object Unknown : ContentConverter {
    override fun convert(file: File): List<Content> = throw UnsupportedOperationException("No converter associate with path '${file.absolutePath}'")

}