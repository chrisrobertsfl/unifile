package com.ingenifi.unifile.v2.generators.text

import com.ingenifi.unifile.v2.model.*
import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class FileData(
    val title: Title,
    val summary: Summary,
    val content: Content,
    val lastUpdated: LastUpdated,
    val keywords: Keywords
) {
    companion object {
        fun from(file: File): FileData {
            val extractor = FileSectionExtractor(file.readText())

            val lastUpdated = determineLastUpdated(file, extractor.lastUpdated().text)
            val title = determineTitle(file, extractor.title().text)
            val summary = extractor.summary()
            val content = extractor.content()
            val keywords = extractor.keywords()

            return FileData(
                title = title,
                summary = summary,
                content = content,
                lastUpdated = lastUpdated,
                keywords = keywords
            )
        }

        private fun determineLastUpdated(file: File, extractedLastUpdated: String): LastUpdated {
            if (extractedLastUpdated.isNotBlank()) return LastUpdated(extractedLastUpdated)

            // Extract date from file name or use current date
            val datePattern = "\\d{8}".toRegex()
            val dateMatch = datePattern.find(file.name)
            val dateStr = dateMatch?.value ?: LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))
            return LastUpdated(dateStr)
        }

        private fun determineTitle(file: File, extractedTitle: String): Title {
            // If a title is extracted from the content, use it
            if (extractedTitle.isNotBlank()) return Title(extractedTitle)

            // Extract title from the file name by removing the extension (if any) and replacing dashes with spaces
            val titleFromFileName = file.nameWithoutExtension.replace("-", " ").trim()

            return Title(titleFromFileName)
        }

    }
}

