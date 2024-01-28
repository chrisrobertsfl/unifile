package com.ingenifi.unifile.model.document



sealed interface TitleText : BodyText {
    object None : TitleText {
        override val content: String get() = ""
    }

    data class Title(override val content: String) : TitleText
}