package com.ingenifi.unifile

import io.kotest.core.spec.style.StringSpec
import org.apache.lucene.analysis.core.StopFilter
import org.apache.lucene.analysis.core.WhitespaceAnalyzer
import org.apache.lucene.analysis.shingle.ShingleFilter
import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute
import java.io.StringReader

class LuceneSpike : StringSpec({

    "extract" {
        val text = "The quick brown fox jumps over the lazy dog."
        val keywords = extractKeywords(text)
        println(keywords)
    }
})


fun extractKeywords(text: String): List<String> {
    // Initialize the analyzer within the try-with-resources block
    try {
        val analyzer = StandardAnalyzer()
        val keywords = mutableListOf<String>()

        val tokenStream = analyzer.tokenStream(null, StringReader(text))
        val charTermAttribute = tokenStream.addAttribute(CharTermAttribute::class.java)

        while (tokenStream.incrementToken()) {
            val term = charTermAttribute.toString()
            keywords.add(term)
        }

        // Calculate the maximum number of unique terms allowed (50% of original content)
        val maxUniqueTerms = keywords.size / 2

        // Return only the first half of unique terms
        return keywords.distinct().take(maxUniqueTerms)
    } catch (e: Exception) {
        // Handle exceptions if necessary
        e.printStackTrace()
    }

    // Return an empty list if an exception occurs
    return emptyList()
}
