package com.ingenifi.unifile.model.document

data class Heading(val headingName : HeadingName = HeadingName.None, val sectionNumber: SectionNumber, val title: TitleText) {

    fun isNameAndTitlePresent() = headingName != HeadingName.None && title != TitleText.None
}