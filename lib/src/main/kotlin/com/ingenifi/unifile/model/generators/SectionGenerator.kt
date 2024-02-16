package com.ingenifi.unifile.model.generators

import com.ingenifi.unifile.model.document.Section

interface SectionGenerator {
    fun generate() = listOf<Section>()
     fun numberOfFilesProcessed(): Int = 1

}