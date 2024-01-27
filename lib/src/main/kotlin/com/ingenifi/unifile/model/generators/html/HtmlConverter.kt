package com.ingenifi.unifile.model.generators.html

import java.io.*

class HtmlConverter {

    fun convert(file : File) = convert(file.readText())
    fun convert(htmlContent: String): String {
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
            return if (process.waitFor() == 0) output.toString() else ""
        } catch (e: Exception) {
            // Handle any exceptions that may occur
            e.printStackTrace()
            return ""
        }
    }
}
