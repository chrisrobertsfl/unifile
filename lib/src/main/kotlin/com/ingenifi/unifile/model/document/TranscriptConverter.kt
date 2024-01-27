package com.ingenifi.unifile.model.document

import java.io.File

class TranscriptConverter {
fun convert(file: File) : Transcript {
    val text = file.readText()
    return Transcript(summary(text), detail(text))
}

    private fun summary(text : String): String = extractSection(text, "BEGIN_SUMMARY", "END_SUMMARY")
    private fun detail(text : String): String = extractSection(text, "BEGIN_DETAIL", "END_DETAIL")

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

data class Transcript(val summary : String, val detail : String)