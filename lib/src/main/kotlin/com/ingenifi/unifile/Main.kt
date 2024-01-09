package com.ingenifi.unifile

//fun main(args: Array<String>) {
//    System.exit(CommandLine(UniFileCli()).execute(*args))
//}




import org.apache.tika.Tika
import org.slf4j.LoggerFactory
import java.io.File

fun main(args: Array<String>) {
    val logger = LoggerFactory.getLogger("MainKt")

    // Check if a file path is provided
    if (args.isEmpty()) {
        logger.error("No file path provided.")
        return
    }

    val filePath = args[0]
    val file = File(filePath)

    // Check if the file exists
    if (!file.exists()) {
        logger.error("File not found: $filePath")
        return
    }

    try {
        val tika = Tika()
        val mimeType = tika.detect(file)
        logger.info("Detected MIME type: $mimeType")

        val text = tika.parseToString(file)

        if (text.isBlank()) {
            logger.warn("The file was parsed but no text was extracted.")
        } else {
            println(text)
        }
    } catch (e: Exception) {
        logger.error("Error processing file", e)
    }
}
