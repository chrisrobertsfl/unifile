package com.ingenifi.unifile.verbosity

interface VerbosePrinting {
    fun verbosePrint(message: String)
    fun verbosePrint(message: String, withLevel: Int)
}