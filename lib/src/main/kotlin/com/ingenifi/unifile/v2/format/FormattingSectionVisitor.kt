package com.ingenifi.unifile.v2.format

import com.ingenifi.unifile.v2.model.*

class FormattingSectionVisitor : SectionVisitor {
    private var text: StringBuilder = java.lang.StringBuilder()
    fun getFormattedText() = text.toString()
    override fun visitId(id: Id) {
        text.appendLine("<!-- Section ID: ${id.number.asSectionId()} -->")
    }

    private fun Int.asSectionId() = "S%04d".format(this)

    override fun visitTitle(title: Title) {
        text.appendLine("## ${title.text}")
    }

    override fun visitMetadataTitle(title: Title) {
        text.appendLine()
        text.appendLine("**${title.text}:**")
    }

    override fun visitKeywords(keywords: Keywords) {
        text.append("- **${keywords.title.text}**: [")
        text.append(keywords.list.joinToString(separator = ", "))
        text.appendLine("]")
    }

    override fun visitLastUpdated(lastUpdated: LastUpdated) {
        text.appendLine("- **Last Updated**: ${lastUpdated.text}")
    }

    override fun visitSummary(summary: Summary) {
        text.appendLine("- **${summary.title.text}**: ${summary.text}")
    }

    override fun visitContent(content: Content) {
        text.appendLine()
        text.appendLine("**${content.title.text}:**")
        text.appendLine(content.text)
    }

    override fun visitRelatedSections(relatedSections: RelatedSections) {
        text.appendLine()
        text.append("**${relatedSections.title.text}:** [")
        text.append(relatedSections.list.map { it.number }.joinToString(separator = ", ") { it.asSectionId() })
        text.appendLine("]")
    }

}