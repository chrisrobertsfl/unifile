package com.ingenifi.unifile.model.document

import java.io.File

data class PdfGenerator(val config: SectionGenerator.Config, val number: Int, val file: File, val headingNameString: String = "Pdf Document", val detail: String = PdfConverter().convert(file)) :
    SectionGenerator by FileGenerator(config, number, file, Name(headingNameString), detail)

data class WordGenerator(val config: SectionGenerator.Config, val number: Int, val file: File, val headingNameString: String = "Word Document", val detail: String = WordConverter().convert(file)) :
    SectionGenerator by FileGenerator(config, number, file, Name(headingNameString), detail)

data class PowerPointGenerator(val config: SectionGenerator.Config, val number: Int, val file: File, val headingNameString: String = "PowerPoint Document", val detail: String = PowerPointConverter().convert(file)) :
    SectionGenerator by FileGenerator(config, number, file, Name(headingNameString), detail)

