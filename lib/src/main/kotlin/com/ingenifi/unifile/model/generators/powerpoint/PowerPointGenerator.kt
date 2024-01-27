package com.ingenifi.unifile.model.generators.powerpoint

import com.ingenifi.unifile.model.generators.FileGenerator
import com.ingenifi.unifile.model.document.Name
import com.ingenifi.unifile.model.generators.SectionGenerator
import com.ingenifi.unifile.model.generators.SectionGeneratorConfig
import java.io.File

data class PowerPointGenerator(val config: SectionGeneratorConfig, val number: Int, val file: File, val headingNameString: String = "PowerPoint Document", val detail: String = PowerPointConverter().convert(file)) :
    SectionGenerator by FileGenerator(config, number, file, Name(headingNameString), detail = detail)