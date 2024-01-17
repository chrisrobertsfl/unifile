package com.ingenifi.unifile.formatter

import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.io.PrintWriter

class HtmlConverter {
    fun convert(htmlContent: String): String? {
        try {
            val processBuilder = ProcessBuilder("pandoc", "-f", "html", "-t", "plain")
            processBuilder.redirectErrorStream(true)
            val process = processBuilder.start()
            val writer = PrintWriter(OutputStreamWriter(process.outputStream))
            writer.write(htmlContent)
            writer.close()
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            val output = StringBuilder()
            var line: String?

            while (reader.readLine().also { line = it } != null) {
                output.append(line).append("\n")
            }
            return if (process.waitFor() == 0) output.toString() else  null
        } catch (e: Exception) {
            // Handle any exceptions that may occur
            e.printStackTrace()
            return null
        }
    }
}
