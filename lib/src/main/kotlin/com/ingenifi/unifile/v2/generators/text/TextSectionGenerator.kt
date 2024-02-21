package com.ingenifi.unifile.v2.generators.text

import com.ingenifi.unifile.model.generators.KeywordExtractor
import com.ingenifi.unifile.v2.model.Id
import com.ingenifi.unifile.v2.model.Section
import com.ingenifi.unifile.v2.model.SectionMetadata
import java.io.File

data class TextSectionGenerator(val number: Int, val file: File, val keywordExtractor: KeywordExtractor = KeywordExtractor()) {
    fun generate(): List<Section> {
        val fileData = FileData.from(file)
        val id = Id(number = number)
        val metadata = SectionMetadata(summary = fileData.summary, keywords = fileData.keywords, lastUpdated = fileData.lastUpdated)
        val section = Section(id = id, title = fileData.title, metadata = metadata, content = fileData.content)
        return listOf(section)
    }
}