package com.ingenifi.unifile.content

data class KeywordExtractor(val percentage: Double = 0.5, val stopWords: List<String>) {

    fun extract(text: String): List<String> {

        val words = text.split("\\s+".toRegex()).map { it.lowercase().trim() }.map { it.replace(Regex("[^\\w\\s]"), "") }
        val filteredWords = words.filterNot { it in stopWords }.filter { it.length > 2 }

        val frequencyMap = filteredWords.groupingBy { it }.eachCount()
        val sortedByFrequency = frequencyMap.entries.sortedByDescending { it.value }

        val numberOfKeywords = (sortedByFrequency.size * percentage).toInt()
        return sortedByFrequency.take(numberOfKeywords).map { it.key }
    }

    fun extractFromFileName(fileName: String): List<String> = fileName.substringBeforeLast(".").split(Regex("\\W+")).filter { it.isNotBlank() }

}

