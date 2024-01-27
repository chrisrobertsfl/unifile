package com.ingenifi.unifile.model.document

data class TableOfContents(val header : String = "Table Of Contents", val headings : List<Heading>)