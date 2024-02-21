package com.ingenifi.unifile.v2.generators.text

import com.ingenifi.unifile.v2.model.*
import java.io.File

data class FileData(val title: Title, val summary: Summary, val content: Content, val lastUpdated: LastUpdated, val keywords: Keywords) {
    companion object {
        fun from(file: File): FileData {
            val extractor = FileSectionExtractor(file.readText())
            return FileData(title = extractor.title(), summary = extractor.summary(), content = extractor.content(), lastUpdated = extractor.lastUpdated(), keywords = extractor.keywords())
        }
    }
}