package com.ingenifi.unifile.formatter.toc

data class SectionNumber(val levels: List<Int>) {
    constructor(vararg levels: Int) : this(levels.toList())

    override fun toString(): String = levels.joinToString(".")
}