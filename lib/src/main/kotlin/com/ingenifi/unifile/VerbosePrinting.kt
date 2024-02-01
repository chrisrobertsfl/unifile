package com.ingenifi.unifile

interface VerbosePrinting {
    fun verbosePrint(message: String)
    fun verbosePrint(message: String, withLevel: Int)
}

data class VerbosePrinter(val verbose: Boolean, val level: Int) : VerbosePrinting {
    constructor(verbosity: Verbosity) : this(verbosity.verbose, verbosity.level)
    override fun verbosePrint(message: String): Unit = if (verbose) printMessage(message) else Unit
    override fun verbosePrint(message: String, withLevel: Int) = if (verbose) printMessage(message, withLevel = withLevel) else Unit
    private fun printMessage(message: String, withLevel: Int = level) = println(format(message, withLevel))
    private fun format(message: String, withLevel: Int = level): String = indentation(withLevel) + "o $message"
    private fun indentation(level: Int): String = " ".repeat(INDENT * level)

    companion object {
        const val INDENT = 2
    }
}

data class Verbosity(val verbose : Boolean, val level : Int) {
    fun increasedLevel(by : Int = 1): Int = level + by
    fun increasingBy(by: Int = 1): Verbosity = copy(level = level + by)
    companion object {
        val NONE: Verbosity = Verbosity(false, 0)
    }
}