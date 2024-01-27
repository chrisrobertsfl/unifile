package com.ingenifi.unifile.model.document

data class Document(val tableOfContents : TableOfContents, val body : Body) {
    constructor(sections : List<Section>) : this(tableOfContents = TableOfContents(headings = sections.map { it.heading }), body = Body(sections))
}

