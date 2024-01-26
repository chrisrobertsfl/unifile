package com.ingenifi.unifile.formatter.toc

data class Entry(val prefix : String = "", val headingNumber: HeadingNumber, val title: String)

data class HeadingNumber(val levels : List<Int>) {

    fun asString(): String = levels.joinToString(".")

}

