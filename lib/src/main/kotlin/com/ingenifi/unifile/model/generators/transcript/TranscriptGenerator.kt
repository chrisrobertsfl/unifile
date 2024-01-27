package com.ingenifi.unifile.model.generators.transcript

import com.ingenifi.unifile.model.generators.FileGenerator
import com.ingenifi.unifile.model.document.Name
import com.ingenifi.unifile.model.generators.SectionGenerator
import com.ingenifi.unifile.model.generators.SectionGeneratorConfig
import java.io.File

data class TranscriptGenerator(val config: SectionGeneratorConfig, val number: Int, val file: File, val headingNameString: String = "Transcript Document", private val transcript : Transcript = TranscriptConverter().convert(file)) :
    SectionGenerator by FileGenerator(config, number, file, Name(headingNameString), summary = transcript.summary, detail = transcript.detail)