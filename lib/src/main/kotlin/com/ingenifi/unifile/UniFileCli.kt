package com.ingenifi.unifile

import com.google.common.base.Stopwatch
import com.google.common.base.Stopwatch.createStarted
import com.ingenifi.unifile.ParameterStore.Companion.loadProperties
import com.ingenifi.unifile.formatter.toc.TableOfContents
import com.ingenifi.unifile.input.InputPaths
import com.ingenifi.unifile.model.generators.KeywordExtractor
import com.ingenifi.unifile.output.FileOutputPath
import com.ingenifi.unifile.output.OutputPath
import com.ingenifi.unifile.output.OutputPath.Companion.from
import com.ingenifi.unifile.verbosity.VerbosePrinter
import com.ingenifi.unifile.verbosity.Verbosity
import org.slf4j.LoggerFactory
import picocli.CommandLine
import picocli.CommandLine.usage
import java.util.concurrent.Callable

@CommandLine.Command(
    name = "unifile", mixinStandardHelpOptions = true, description = ["Utility to process and combine files."]
)
class UniFileCli : Callable<Int> {

    private val logger by lazy { LoggerFactory.getLogger(UniFileCli::class.java) }

    @CommandLine.Option(names = ["-i", "--input"], description = ["Input files or directories"])
    private var inputPaths: Array<String> = arrayOf()

    @CommandLine.Option(names = ["-o", "--output"], description = ["Output file path"])
    private var outputPath: String? = null

    @CommandLine.Option(names = ["-p", "--properties"], description = ["Properties file path"])
    private var propertiesFilePath: String? = null

    @CommandLine.Option(names = ["-v", "--verbose"], description = ["Enable verbose output"])
    private var verbose: Boolean = false


    override fun call(): Int {
        val verbosity = Verbosity(verbose = verbose, level = 0)
        val printer = VerbosePrinter(verbosity)
        if (inputPaths.isEmpty()) {
           usage(this, System.out)
            return 1
        }
        return try {
            val output : OutputPath = from(pathName = outputPath)
            val uniFile = UniFileRunner(input = InputPaths(paths = inputPaths.toList()), verbosity = verbosity)
            val stopwatch = createStarted()
            uniFile.combineFiles(output)
            val elapsed = stopwatch.stop().elapsed().toSeconds()
            printer.verbosePrint("Processing took $elapsed seconds")
            if (output is FileOutputPath) printer.verbosePrint("Combined file created: ${output.path}") else printer.verbosePrint("Completed with output written to console")
            0
        } catch (e: Exception) {
            logger.error("Failed to process files: {}", e.message, e)
            1
        }
    }
}