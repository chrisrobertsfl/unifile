package com.ingenifi.unifile.model.generators

import com.ingenifi.unifile.model.document.Section
import com.ingenifi.unifile.model.document.Sections

interface SectionsGenerator {
    fun generate() : Sections = Sections(list = emptyList<Section>())

}