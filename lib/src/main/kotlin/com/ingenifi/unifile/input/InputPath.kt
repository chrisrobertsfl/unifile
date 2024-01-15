package com.ingenifi.unifile.input

import java.io.File

class InputPath(private val pathName: String) {
    fun findFiles(): List<File> = when {
        pathName == "." -> File(".").walkTopDown().filter { it.isFile }.toList()
        File(pathName).isDirectory -> File(pathName).walkTopDown().filter { it.isFile }.toList()
        else -> listOf(File(pathName))
    }
}