package com.ingenifi.unifile.model.generators.html

import com.ingenifi.unifile.model.generators.FileGenerator
import com.ingenifi.unifile.model.document.Name
import com.ingenifi.unifile.model.generators.SectionGenerator
import com.ingenifi.unifile.model.generators.SectionGeneratorConfig
import java.io.File

data class HtmlGenerator(val config: SectionGeneratorConfig, val number: Int, val file: File, val headingNameString: String = "Html Document", val detail: String = HtmlConverter().convert(file)) :
    SectionGenerator by FileGenerator(config, number, file, Name(headingNameString), detail = detail)