package com.ingenifi.unifile.model.document

data class SectionNumber(val levels : List<Level>) {
    constructor(number : Int) : this(listOf(Level(number)))
    fun append(number : Int) = SectionNumber(levels = levels + listOf(Level(number)))
}