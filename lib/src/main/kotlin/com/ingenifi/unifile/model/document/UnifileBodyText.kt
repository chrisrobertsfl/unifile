package com.ingenifi.unifile.model.document


data class UnifileBodyText(
    val headingName: HeadingName = HeadingName.None,
    val keywords: KeywordsText = KeywordsText.None,
    val summary: SummaryText = SummaryText.None,
    val detail: DetailText = DetailText.None
) : BodyText {
    private val headingNameContent = when (headingName) {
        is HeadingName.None -> ""
        else -> "${headingName.content} "
    }

    override val content: String = buildString {
        appendKeywords()
        appendSummary()
        appendDetail()
    }

    private fun StringBuilder.appendKeywords() {
        appendHeading("Keywords")
        appendLine(keywords.content)
    }

    private fun StringBuilder.appendSummary() {
        appendHeading("Summary")
        appendLine(summary.content)
    }

    private fun StringBuilder.appendDetail() {
        appendHeading("Detail")
        appendLine(detail.content)
    }

    private fun StringBuilder.appendHeading(heading: String) = appendLine("- ${headingNameContent}$heading")

}

sealed interface KeywordsText : BodyText {
    object None : KeywordsText {
        override val content: String get() = "(no keywords)"

        override fun toString(): String = "None(keywords=[], content=$content)"
    }

    data class Keywords(val keywords: List<String> = listOf(), override val content: String = keywords.joinToString(", ")) : KeywordsText
}

sealed interface SummaryText : BodyText {
    object None : SummaryText {
        override val content: String get() = "(no summary)"
    }

    data class Summary(override val content: String) : SummaryText
}

sealed interface DetailText : BodyText {
    object None : DetailText {
        override val content: String get() = "(no detail)"
    }

    data class Detail(override val content: String) : DetailText

}