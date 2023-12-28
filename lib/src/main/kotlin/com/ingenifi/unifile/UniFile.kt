package com.ingenifi.unifile

import org.apache.tika.Tika
import org.apache.tika.exception.TikaException
import org.apache.tika.exception.ZeroByteFileException
import org.apache.tika.metadata.Metadata
import org.slf4j.LoggerFactory
import picocli.CommandLine
import java.io.File
import java.io.FileInputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.Callable


@CommandLine.Command(
    name = "unifile",
    mixinStandardHelpOptions = true,
    description = ["Utility to process and combine files."]
)class UniFileCli : Callable<Int> {

    private val logger = LoggerFactory.getLogger(UniFileCli::class.java)

    @CommandLine.Option(names = ["-i", "--input"], description = ["Input files or directories"])
    private var inputPaths: Array<String> = arrayOf()


    @CommandLine.Option(names = ["-o", "--output"], description = ["Output file path"])
    private var outputPath: String? = null


    @CommandLine.Option(names = ["-v", "--verbose"], description = ["Enable verbose output"])
    private var verbose: Boolean = false

    override fun call(): Int {
        if (inputPaths.isEmpty()) {
            CommandLine.usage(this, System.out)
            return 1
        }
        return try {
            val output = OutputPath(outputPath)
            val input = InputPaths(inputPaths.toList())
            UniFile(input).combineFiles(output)
            logger.info("Combined file created: {}", output.path)
            0
        } catch (e: Exception) {
            logger.error("Failed to process files: {}", e.message, e)
            1
        }
    }
}
data class UniFile(val input: InputPaths, val maxFileSizeMB: Int = 19, val ejectBlankLines: Boolean = true) {
    private val logger = LoggerFactory.getLogger(UniFile::class.java)

    fun combineFiles(output: OutputPath) {
        val fileSplitter = FileSplitter(maxFileSizeMB, output.path!!)

        input.list.flatMap { it.findFiles() }
            .mapNotNull { retrieve(it) }
            .forEach { uniFileEntry ->
                val formattedContent = format(uniFileEntry)
                fileSplitter.addContent(formattedContent)
            }

        fileSplitter.finalizeSplitting()
    }


    fun retrieve(file: File): UniFileEntry? = FileInputStream(file).use {
        logger.info("Processing file ${file.absolutePath}")
        val metadata = Metadata()
        try {
            val contents = Tika().parseToString(it, metadata)
            if (contents.isBlank()) {
                logger.warn("No content - skipping file: {}", file.absolutePath)
                return null
            }
            val entry = UniFileEntry(contents = contents, fileName = file.name, contentType = metadata.get("Content-Type"))
            entry
        } catch(e : ZeroByteFileException) {
            logger.warn("No bytes - skipping file: {}", file.absolutePath)
            null
        }
        catch( e : TikaException){
            logger.warn("{}} - skipping file: {}", e.message, file.absolutePath)
            null
        }
    }

    fun format(uniFileEntry: UniFileEntry): String = buildString {
        append("File Name: ${uniFileEntry.fileName}\n")
        append("Content Type: ${uniFileEntry.contentType}\n")
        val contents = if (ejectBlankLines) uniFileEntry.contents.skipBlanks() else uniFileEntry.contents
        append("Contents:\n${contents}\n")
        append("---\n")
    }

    private fun String.skipBlanks(): String {
        return this.lines()
            .filter { it.isNotBlank() }  // Keep only non-blank lines
            .joinToString("\n")          // Rejoin the lines with a newline character
    }


}

data class UniFileEntry(val fileName: String = "", val contentType: String = "", val contents: String = "")

class InputPaths(paths: List<String>) {
    val list: List<InputPath> = paths.map { InputPath(it) }
}

class InputPath(private val path: String) {
    fun findFiles(): List<File> = when {
        path == "." -> File(".").walkTopDown().filter { it.isFile }.toList()
        File(path).isDirectory -> File(path).walkTopDown().filter { it.isFile }.toList()
        else -> listOf(File(path))
    }
}


data class OutputPath(var path: String?) {
    init {
        if (path.isNullOrEmpty()) {
            path = generatePathName()
        }
    }

    fun write(text: String) {
        File(path!!).apply {
            parentFile?.mkdirs()  // Ensure the directory exists
            writeText(text)
        }
    }

    private fun generatePathName(): String {
        return "unifile-${LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MMM-dd-HH:mm:ss"))}.txt"
    }
}

class FileSplitter(private val maxFileSizeMB: Int, private val basePath: String) {
    private var currentFileSize = 0L
    private var fileIndex = 0
    private val currentContent = StringBuilder()

    fun addContent(content: String) {
        if (currentFileSize + content.toByteArray().size > maxFileSizeMB * 1024 * 1024) {
            writeCurrentContent()
        }
        currentContent.append(content)
        currentFileSize += content.toByteArray().size
    }

    fun finalizeSplitting() {
        if (currentContent.isNotEmpty()) {
            writeCurrentContent()
        }
    }

    private fun writeCurrentContent() {
        val filePath = if (fileIndex > 0) "${basePathWithoutExtension()}_$fileIndex.txt" else basePath
        File(filePath).writeText(currentContent.toString())
        currentContent.clear()
        currentFileSize = 0
        fileIndex++
    }

    private fun basePathWithoutExtension() = basePath.substringBeforeLast(".")
}