package com.ingenifi.unifile.content

import java.nio.charset.StandardCharsets

data class KeywordExtractor(val percentage: Double = 0.025, val stopWords: List<String> = gatherStopWords(), val filters: List<(String) -> Boolean> = listOf()
) {

    fun extract(text: String): List<String> {

        val words = text.split("\\s+".toRegex()).map { it.lowercase().trim() }.map { it.replace(Regex("[^\\w\\s]"), "") }
        val filteredWords = words.filterNot { it in stopWords }.filter { word -> filters.all{ filter -> filter(word)} }

        val frequencyMap = filteredWords.groupingBy { it }.eachCount()
        val sortedByFrequency = frequencyMap.entries.sortedByDescending { it.value }

        val numberOfKeywords = (sortedByFrequency.size * percentage).toInt()
        return sortedByFrequency.take(numberOfKeywords).map { it.key }
    }

    companion object {
        private fun gatherStopWords(): List<String> {
            val resourceAsStream = this::class.java.getResourceAsStream("/all-stop-words.txt")
            if (resourceAsStream != null) {
                return resourceAsStream.bufferedReader(StandardCharsets.UTF_8).readLines()
            } else {
                throw IllegalArgumentException("Resource not found: /all-stop-words.txt")
            }
        }
    }


}

