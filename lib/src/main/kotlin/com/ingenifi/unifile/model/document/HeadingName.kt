package com.ingenifi.unifile.model.document

sealed interface HeadingName {
    val content : String

    object None : HeadingName {
        override val content: String get() = ""
    }
}