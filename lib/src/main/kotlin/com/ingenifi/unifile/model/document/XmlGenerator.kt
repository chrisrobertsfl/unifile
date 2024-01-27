package com.ingenifi.unifile.model.document

import java.io.File

data class XmlGenerator(val config: SectionGenerator.Config, val number: Int, val file: File, val headingNameString: String = "Xml Document", val detail: String = XmlConverter().convert(file)) :
    SectionGenerator by FileGenerator(config, number, file, Name(headingNameString), detail)