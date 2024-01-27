package com.ingenifi.unifile.verbosity

data class Verbosity(val verbose : Boolean, val level : Int) {
    fun increasedLevel(by : Int = 1): Int = level + by

    fun increasingBy(by: Int = 1): Verbosity = copy(level = level + by)

    companion object {
        val NONE: Verbosity = Verbosity(false, 0)
    }

}

