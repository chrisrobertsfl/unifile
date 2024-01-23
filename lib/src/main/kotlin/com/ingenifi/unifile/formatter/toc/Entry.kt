package com.ingenifi.unifile.formatter.toc

data class Entry(val headingNumber: HeadingNumber, val title: String)

interface HeadingNumber {

    val type : String
    fun asString(): String = levels.joinToString(".")
    val levels : List<Int>
}
data class DocumentNumber(override val levels: List<Int>) : HeadingNumber {

    override val type: String = "Document"
    constructor(vararg levels: Int) : this(levels.toList())
}


data class SectionNumber(override val levels: List<Int>) : HeadingNumber {

    override val type: String = "Section"
    constructor(vararg levels: Int) : this(levels.toList())
}