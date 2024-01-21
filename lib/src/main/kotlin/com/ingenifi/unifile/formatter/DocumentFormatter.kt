package com.ingenifi.unifile.formatter

interface DocumentFormatter {
    fun format(number: Int): String
    fun lastNumber(): Int
}
