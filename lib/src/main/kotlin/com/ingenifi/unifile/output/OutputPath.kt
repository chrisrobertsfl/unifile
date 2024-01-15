package com.ingenifi.unifile.output

sealed interface OutputPath {

    companion object {
        fun from(pathName: String?): OutputPath = if (pathName == null) ConsoleOutputPath else FileOutputPath(pathName)
    }

    fun write(text: String)

    object ConsoleOutputPath : OutputPath {
        override fun write(text: String) {
            println(text)
        }
    }
}