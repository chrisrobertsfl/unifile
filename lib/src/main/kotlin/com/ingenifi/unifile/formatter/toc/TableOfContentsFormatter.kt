package com.ingenifi.unifile.formatter.toc

fun interface TableOfContentsFormatter {
    fun format(entries: List<Entry>): String
}