package com.ingenifi.unifile.model.document

import java.io.File

data class TextGenerator(val config: SectionGenerator.Config, val number: Int, val file: File, val headingNameString: String = "Text Document") :
    SectionGenerator by FileGenerator(config, number, file, Name(headingNameString))