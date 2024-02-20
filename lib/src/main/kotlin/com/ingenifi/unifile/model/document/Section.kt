package com.ingenifi.unifile.model.document

import java.time.LocalDate

data class Section(val heading : Heading, val lastUpdated : LocalDate = LocalDate.now(), val keywords : List<String> = listOf(), val summary : String = "", val bodyText : BodyText)