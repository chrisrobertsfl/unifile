package com.ingenifi.unifile.model.generators.xml

import com.ingenifi.unifile.model.document.*
import com.ingenifi.unifile.model.generators.FileGenerator
import com.ingenifi.unifile.model.generators.SectionGenerator
import com.ingenifi.unifile.model.generators.SectionGeneratorConfig
import java.io.File

data class XmlGenerator(val config: SectionGeneratorConfig, val number: Int, val file: File, val headingNameString: String = "Xml Document", val detail: String = XmlConverter().convert(file)) :
    SectionGenerator by FileGenerator(config, number, file, Name(headingNameString), detail = detail)