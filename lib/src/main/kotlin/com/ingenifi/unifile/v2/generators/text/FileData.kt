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

            val lastUpdated = extractor.lastUpdated().takeIfNotEmpty() ?: file.determineLastUpdated()
            val title = extractor.title().takeIfNotEmpty() ?: file.deriveTitle(lastUpdated)
            val summary = extractor.summary()
            val content = extractor.content()
            val keywords = extractor.keywords()

            return FileData(title, summary, content, lastUpdated, keywords)
        }

        private fun LastUpdated.takeIfNotEmpty(): LastUpdated? = takeIf { it.text.isNotEmpty() }
        private fun Title.takeIfNotEmpty(): Title? = takeIf { it.text.isNotEmpty() }

        private fun File.deriveTitle(lastUpdated: LastUpdated): Title {
            val titleWithoutDate = nameWithoutExtension
                .removePrefix(lastUpdated.text)
                .replaceFirst("^\\d{8}-", "")
                .replace('-', ' ')
                .trim()
            return Title(titleWithoutDate)
        }

        private fun File.determineLastUpdated(): LastUpdated {
            val datePattern = "\\d{8}".toRegex()
            val extractedDate = datePattern.find(name)?.value
            val fileDate = extractedDate ?: Files.readAttributes(toPath(), BasicFileAttributes::class.java)
                .creationTime()
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
                .format(DateTimeFormatter.ofPattern("yyyyMMdd"))

            return LastUpdated(fileDate)
        }
    }
}
