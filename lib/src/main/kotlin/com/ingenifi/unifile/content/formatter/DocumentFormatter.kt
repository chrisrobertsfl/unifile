package com.ingenifi.unifile.content.formatter

 interface DocumentFormatter {
    fun format(number: Int): String
    fun lastNumber(): Int
}