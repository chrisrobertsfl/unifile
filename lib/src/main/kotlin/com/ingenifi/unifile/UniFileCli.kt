package com.ingenifi.unifile

import com.ingenifi.unifile.input.InputPaths
import com.ingenifi.unifile.output.FileOutputPath
import com.ingenifi.unifile.output.OutputPath
import org.slf4j.LoggerFactory
import picocli.CommandLine
import java.util.concurrent.Callable

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
            val output = OutputPath.from(outputPath)
            val input = InputPaths(inputPaths.toList())
            UniFile(input, verbose = verbose).combineFiles(output)
            if (output is FileOutputPath) {
                if (verbose) println("o Combined file created: ${output.path}")
            }
            else {
                if (verbose) println("o Completed with output written to console")
            }
            0
        } catch (e: Exception) {
            logger.error("Failed to process files: {}", e.message, e)
            1
        }
    }
}