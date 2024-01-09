package com.ingenifi.unifile

import org.slf4j.LoggerFactory
import picocli.CommandLine
import java.io.File
import java.nio.file.Path
import java.nio.file.Paths
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.Callable
import kotlin.io.path.extension


@CommandLine.Command(
    name = "unifile", mixinStandardHelpOptions = true, description = ["Utility to process and combine files."]
)
class UniFileCli : Callable<Int> {

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
    fun combineFiles(output: OutputPath) {
        val outputs = input.list.flatMap { it.findFiles() }.mapNotNull {
            DocumentConverter.from(it.extension).convert(it)
        }
        val outputText = UnifyingMerger().merge(outputs)
        output.write(outputText)
    }

}

class InputPaths(paths: List<String>) {
    val list: List<InputPath> = paths.map { InputPath(it) }
}

class InputPath(private val pathName: String) {
    fun findFiles(): List<Path> = when {
        pathName == "." -> File(".").walkTopDown().filter { it.isFile }.map { it.toPath() }.toList()
        File(pathName).isDirectory -> File(pathName).walkTopDown().filter { it.isFile }.map { it.toPath() }.toList()
        else -> listOf(Paths.get(pathName))
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
            // Change to appendText to add to the file rather than overwrite it
            appendText(text)
        }
    }

    private fun generatePathName(): String {
        return "unifile-${LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MMM-dd-HH:mm:ss"))}.txt"
    }
}
