package com.ingenifi.unifile

import picocli.CommandLine

fun main(args: Array<String>) {
    System.exit(CommandLine(UniFileCli()).execute(*args))
}