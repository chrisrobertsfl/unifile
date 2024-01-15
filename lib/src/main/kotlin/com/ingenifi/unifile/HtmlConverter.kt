package com.ingenifi.unifile

import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.io.PrintWriter

class HtmlConverter {
    fun convert(htmlContent: String): String? {
        try {
            // Create a ProcessBuilder for the Pandoc command
            val processBuilder = ProcessBuilder("pandoc", "-f", "html", "-t", "plain")

            // Redirect error stream to the output stream
            processBuilder.redirectErrorStream(true)

            // Start the Pandoc process
            val process = processBuilder.start()

            // Write the HTML content to the process's input stream
            val writer = PrintWriter(OutputStreamWriter(process.outputStream))
            writer.write(htmlContent)
            writer.close()

            // Read the output of the process
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            val output = StringBuilder()
            var line: String?

            while (reader.readLine().also { line = it } != null) {
                output.append(line).append("\n")
            }

            // Wait for the process to complete
            val exitCode = process.waitFor()

            if (exitCode == 0) {
                // Conversion successful, return the plain text
                return output.toString()
            } else {
                // Conversion failed, return null
                return null
            }
        } catch (e: Exception) {
            // Handle any exceptions that may occur
            e.printStackTrace()
            return null
        }
    }
}
