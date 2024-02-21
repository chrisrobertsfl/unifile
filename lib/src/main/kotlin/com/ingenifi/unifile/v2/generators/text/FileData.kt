package com.ingenifi.unifile.v2.generators.text

import com.ingenifi.unifile.v2.model.*
import java.io.File
import java.nio.file.Files
import java.nio.file.attribute.BasicFileAttributes
import java.time.LocalDate
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
            val lastUpdatedText = extractor.lastUpdated().text

            val lastUpdated = if (lastUpdatedText.isBlank()) {
                // Extract date from file name using the new pattern without dashes
                val datePattern = "\\d{8}".toRegex()
                val dateMatch = datePattern.find(file.name)
                val dateStr = dateMatch?.value
                val date = if (dateStr != null) {
                    dateStr // Use the extracted date string directly
                } else {
                    // If no date found in file name, use file creation date
                    val fileAttr = Files.readAttributes(file.toPath(), BasicFileAttributes::class.java)
                    val fileCreationDate = fileAttr.creationTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
                    fileCreationDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"))
                }
                LastUpdated(date)
            } else {
                LastUpdated(lastUpdatedText)
            }

            return FileData(
                title = extractor.title(),
                summary = extractor.summary(),
                content = extractor.content(),
                lastUpdated = lastUpdated,
                keywords = extractor.keywords()
            )
        }
    }
}
