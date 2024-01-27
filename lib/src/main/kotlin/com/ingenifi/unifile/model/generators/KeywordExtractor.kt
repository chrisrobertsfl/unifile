package com.ingenifi.unifile.model.generators

import java.io.File
import java.nio.charset.StandardCharsets

data class KeywordExtractor(
    val percentage: Double = 0.025,
    val stopWords: List<String> = gatherStopWords(),
    val filters: List<(String) -> Boolean> = listOf()
) {

    fun extract(text: String, percentage: Double = this.percentage): List<String> {
        val words = text.split("\\s+".toRegex())
            .map { it.lowercase().trim() }
            .map { it.replace(Regex("[^\\w\\s]"), "") }

        val filteredWords = words
            .filterNot { it in stopWords }
            .filter { word -> filters.all { filter -> filter(word) } }
            .filter { it.length > 1 || it.matches(Regex("[a-zA-Z0-9]")) } // Allow single letters/numbers, filter out single non-alphabetic characters

        val frequencyMap = filteredWords.groupingBy { it }.eachCount()
        val sortedByFrequency = frequencyMap.entries.sortedByDescending { it.value }

        val numberOfKeywords = (sortedByFrequency.size * percentage).toInt()
        return sortedByFrequency.take(numberOfKeywords).map { it.key }
    }

    fun extractKeywords(file : File): List<String> {
        val pathKeywords = file.toPath().parent?.iterator()?.asSequence()?.map { it.toString().replace(Regex("\\W+"), " ").trim() }?.filter { it.isNotEmpty() }?.toList() ?: emptyList()
        val normalizedFileName = file.name.substringBeforeLast('.').replace(Regex("\\W+"), " ").trim()
        val keywords = pathKeywords + listOf(normalizedFileName)
        val extension = file.extension
        return if (extension.isNotEmpty()) keywords + extension else keywords
    }

    companion object {
        private fun gatherStopWords(): List<String> {
            val resourceAsStream = this::class.java.getResourceAsStream("/all-stop-words.txt")
            return resourceAsStream?.bufferedReader(StandardCharsets.UTF_8)?.readLines()
                ?: throw IllegalArgumentException("Resource not found: /all-stop-words.txt")
        }
    }
}
