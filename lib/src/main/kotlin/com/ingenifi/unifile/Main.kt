package com.ingenifi.unifile

import org.apache.tika.Tika
import org.apache.tika.metadata.Metadata
import org.slf4j.LoggerFactory
import picocli.CommandLine
import java.io.File
import java.io.FileInputStream
import java.nio.file.Files.isRegularFile
import java.nio.file.Files.walk
import java.nio.file.Paths
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.Callable

fun main(args: Array<String>) {
    System.exit(CommandLine(UniFileCli()).execute(*args))
}



