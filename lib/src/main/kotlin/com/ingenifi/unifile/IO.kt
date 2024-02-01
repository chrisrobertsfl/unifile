package com.ingenifi.unifile

import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class InputPath(private val pathName: String) {
    fun findFiles(): List<File> {
        val file = File(pathName)
        return when {
            file.isDirectory -> file.walkFiles()
            file.exists() -> listOf(file)
            else -> emptyList()
        }
    }

    private fun File.walkFiles(): List<File> = walkTopDown().filter { it.isFile }.toList()
}

class InputPaths(paths: List<String>) {
    val list: List<InputPath> = paths.map { InputPath(it) }
    fun allFiles() = list.flatMap { it.findFiles() }
}

data class FileOutputPath(var path: String?) : OutputPath {
    init {
        if (path.isNullOrEmpty()) {
            path = generatePathName()
        }
    }

    override fun write(text: String) {
        File(path!!).apply {
            parentFile?.mkdirs()
            appendText(text)
        }
    }

    private fun generatePathName(): String = "unifile-${LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MMM-dd-HH:mm:ss"))}.txt"
}

fun interface OutputPath {
    fun write(text: String)

    companion object {
        fun from(pathName: String?): OutputPath = if (pathName == null) ConsoleOutputPath else FileOutputPath(pathName)
    }

    object ConsoleOutputPath : OutputPath {
        override fun write(text: String) = println(text)
    }

    class StringOutputPath : OutputPath {
        lateinit var contents: String
        override fun write(text: String) {
            contents = text
        }
    }
}