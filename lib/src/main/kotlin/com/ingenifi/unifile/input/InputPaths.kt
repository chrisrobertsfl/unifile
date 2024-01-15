package com.ingenifi.unifile.input

class InputPaths(paths: List<String>) {
    val list: List<InputPath> = paths.map { InputPath(it) }
}