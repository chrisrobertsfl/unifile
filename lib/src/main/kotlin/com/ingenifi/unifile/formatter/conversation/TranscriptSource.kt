package com.ingenifi.unifile.formatter.conversation

import com.ingenifi.unifile.formatter.FileSource
import java.io.File

class TranscriptSource(override val file: File) : FileSource {
   private val text = file.readText()

    fun introduction(): String = extractSection(text, "BEGIN_INTRODUCTION", "END_INTRODUCTION")
    override fun description(): String = extractSection(text, "BEGIN_DESCRIPTION", "END_DESCRIPTION")

    private fun extractSection(text: String, startTag: String, endTag: String): String {
        val startIndex = text.indexOf("[$startTag]") + startTag.length + 2
        val endIndex = text.indexOf("[$endTag]")
        return if (startIndex in 0 until endIndex && endIndex >= 0) {
            text.substring(startIndex, endIndex).trim()
        } else {
            "Not Found"
        }
    }
}