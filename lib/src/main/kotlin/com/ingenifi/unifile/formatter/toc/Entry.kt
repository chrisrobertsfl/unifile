package com.ingenifi.unifile.formatter.toc

data class Entry(val headingNumber: HeadingNumber, val title: String)

interface HeadingNumber {

    val type : String
    fun asString(): String = levels.joinToString(".")
    val levels : List<Int>
    data class Delegate(override val type : String, override val levels: List<Int>) : HeadingNumber

}
data class DocumentNumber(override val levels: List<Int>) : HeadingNumber by HeadingNumber.Delegate("Document", levels)
data class SectionNumber(override val levels: List<Int>) : HeadingNumber by HeadingNumber.Delegate("Section", levels)
