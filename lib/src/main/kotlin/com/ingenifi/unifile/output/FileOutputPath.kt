package com.ingenifi.unifile.output

import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class FileOutputPath(var path: String?) : OutputPath {
    init {
        if (path.isNullOrEmpty()) {
            path = generatePathName()
        }
    }

    override fun write(text: String) {
        File(path!!).apply {
            parentFile?.mkdirs()  // Ensure the directory exists
            // Change to appendText to add to the file rather than overwrite it
            appendText(text)
        }
    }

    private fun generatePathName(): String {
        return "unifile-${LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MMM-dd-HH:mm:ss"))}.txt"
    }
}