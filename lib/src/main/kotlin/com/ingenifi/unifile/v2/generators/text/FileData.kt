package com.ingenifi.unifile.v2.generators.text

import com.ingenifi.unifile.v2.model.*
import java.io.File
import java.nio.file.Files
import java.nio.file.attribute.BasicFileAttributes
import java.time.ZoneId
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

            val extractedLastUpdated = extractor.lastUpdated().text
            val lastUpdated = determineLastUpdated(file, extractedLastUpdated)
            val title = determineTitle(file, extractor.title().text, lastUpdated.text)
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
            if (extractedLastUpdated.isNotBlank()) {
                return LastUpdated(extractedLastUpdated)
            }

            val datePattern = "\\d{8}".toRegex()
            datePattern.find(file.name)?.let {
                return LastUpdated(it.value)
            }

            val fileAttr = Files.readAttributes(file.toPath(), BasicFileAttributes::class.java)
            val fileDate = fileAttr.creationTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
            return LastUpdated(fileDate.format(DateTimeFormatter.ofPattern("yyyyMMdd")))
        }

        private fun determineTitle(file: File, extractedTitle: String, extractedLastUpdated: String): Title {
            // If the title is extracted from the content
            if (extractedTitle.isNotBlank()) {
                return Title(extractedTitle)
            }

            // Regex to identify and remove the date prefix from the file name if present
            val datePrefixPattern = "^\\d{8}-".toRegex()
            val fileNameWithoutExtension = file.nameWithoutExtension
            val titleWithoutDatePrefix = datePrefixPattern.replaceFirst(fileNameWithoutExtension, "")
            val title = titleWithoutDatePrefix.replace('-', ' ').trim()
            return Title(title)
        }
    }
}
