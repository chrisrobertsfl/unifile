package com.ingenifi.unifile.model.document

data class Heading(val headingName : HeadingName = HeadingName.None, val sectionNumber: SectionNumber, val title: Title)