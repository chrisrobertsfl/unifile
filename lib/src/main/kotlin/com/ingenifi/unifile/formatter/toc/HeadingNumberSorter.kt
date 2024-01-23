package com.ingenifi.unifile.formatter.toc

import kotlin.math.max

data class HeadingNumberSorter(val headingNumbers: List<HeadingNumber>) {
    fun sortHeadingNumbers(): List<HeadingNumber> {
        return headingNumbers.sortedWith(Comparator { o1, o2 ->
            for (i in 0 until max(o1.levels.size, o2.levels.size)) {
                val o1Level = o1.levels.getOrElse(i) { 0 }
                val o2Level = o2.levels.getOrElse(i) { 0 }
                val result = o1Level.compareTo(o2Level)
                if (result != 0) return@Comparator result
            }
            o1.levels.size.compareTo(o2.levels.size)
        })
    }
}
