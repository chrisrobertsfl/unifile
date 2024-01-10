package com.ingenifi.unifile

import picocli.CommandLine

fun main(args: Array<String>) {
    System.setProperty("java.util.logging.config.file", "path/to/logging.properties")
    System.exit(CommandLine(UniFileCli()).execute(*args))
}