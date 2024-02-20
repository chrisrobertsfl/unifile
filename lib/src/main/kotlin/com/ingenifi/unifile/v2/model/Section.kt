package com.ingenifi.unifile.v2.model

import java.time.LocalDateTime

data class Section(val id: Id, val title: Title, val metadata: SectionMetadata, val content : Content, val relatedSections: RelatedSections) {
    fun accept(visitor: SectionVisitor) = visitor.visitSection(this)
}

data class Id(val number: Int = 0)
data class Title(val text: String = "")
data class SectionMetadata(val title : Title = Title("Metdata"), val keywords: Keywords, val lastUpdated: LastUpdated, val summary: Summary)
data class Keywords(val title : Title = Title("Tags/Keywords"), val list: List<String> = listOf())
data class LastUpdated(val text: String = LocalDateTime.now().toString())
data class Summary(val title : Title = Title("Summary"), val text: String = "")
data class Content(val title : Title = Title("Content"), val text: String = "")
data class RelatedSections(val title : Title = Title("Related Sections"), val list: List<Id> = listOf())

interface SectionVisitor {
    fun visitSection(section: Section) {
        visitId(section.id)
        visitTitle(section.title)
        visitMetadata(section.metadata)
        visitContent(section.content)
        visitRelatedSections(section.relatedSections)
    }

    fun visitId(id: Id)
    fun visitTitle(title: Title)
    fun visitMetadata(metadata: SectionMetadata) {
        visitMetadataTitle(metadata.title)
        visitKeywords(metadata.keywords)
        visitSummary(metadata.summary)
        visitLastUpdated(metadata.lastUpdated)
    }

    fun visitMetadataTitle(title : Title)
    fun visitKeywords(keywords: Keywords)
    fun visitLastUpdated(lastUpdated: LastUpdated)
    fun visitSummary(summary: Summary)
    fun visitContent(content: Content)
    fun visitRelatedSections(relatedSections: RelatedSections)
}


