package com.ingenifi.unifile.model.document

import com.ingenifi.unifile.KeywordExtractor
import com.ingenifi.unifile.Verbosity
import com.ingenifi.unifile.formatter.excel.ExcelConverter
import com.ingenifi.unifile.formatter.excel.WorkSheet
import com.ingenifi.unifile.model.document.SectionGenerator.Config
import com.ingenifi.unifile.resourceAsString
import com.ingenifi.unifile.simpleFile
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.mockk
import java.io.File

class ExcelGeneratorSpecification : StringSpec({

    "generate" {
        val config = Config(keywordExtractor = KeywordExtractor(), verbosity = mockk<Verbosity>())
        val sections = ExcelGenerator(config = config, number = 1, file = simpleFile("xlsx")).generate()
        DocumentGenerator(Document(sections = sections)).generate() shouldBe resourceAsString("expected-excel-document.txt")
    }
})

data class ExcelGenerator(val config: Config, val number: Int, val file: File, val headingNameString: String = "Excel Document") : SectionGenerator {
    private val fileKeywords = config.keywordExtractor.extractKeywords(file)
    override fun generate(): List<Section> {
        val sections = mutableListOf<Section>()
        var sectionNumberCounter: Int = 1
        val worksheetData = ExcelConverter().convert(file)
        sections.add(generateWorkbookSection(worksheetData.keys))
        sections.addAll(worksheetData.map {
            generateWorksheet(name = it.key, worksheet = it.value, sectionNumberCounter++)
        })
        return sections
    }

    private fun generateWorkbookSection(worksheetNames: Set<String>): Section {
        val headingName = Name("Excel Workbook")
        val title = file.name.substringBeforeLast(".xls")
        val heading = Heading(headingName = headingName, sectionNumber = SectionNumber(listOf(Level(number))), title = Title(title))
        val keywords = KeywordsText.Keywords(fileKeywords + worksheetNames)
        val summary = SummaryText.Summary("This is the workbook '$title' containing ${worksheetNames.size} worksheet(s) named: ${worksheetNames.asWorksheetList()}")
        val detail = DetailText.Detail("Subsections represent worksheets contained in this workbook '$title'")
        val text = UnifileBodyText(headingName = headingName, keywords = keywords, summary = summary, detail = detail)
        return Section(heading = heading, text = text)
    }

    private fun Set<String>.asWorksheetList(): String  = joinToString(separator = ", ", prefix = "'", postfix = "'") { it }

    private fun generateWorksheet(name: String, worksheet: WorkSheet, sectionNumberCounter: Int): Section {
        val headingName = Name("Excel Worksheet")
        val title = worksheet.title
        val heading = Heading(headingName = headingName, sectionNumber = SectionNumber(listOf(Level(number), Level(sectionNumberCounter))), title = Title(title))
        val csvDetail = worksheet.toFullCsv()
        val allKeywords = mutableListOf<String>()
        allKeywords.addAll(config.keywordExtractor.extract(csvDetail))
        allKeywords.addAll(fileKeywords)
        allKeywords.add(title)
        allKeywords.add(worksheet.workbookName)
        val keywords = KeywordsText.Keywords(allKeywords)
        val summary = SummaryText.Summary("This is worksheet '$title' of workbook '${worksheet.workbookName}")
        val detail = DetailText.Detail(csvDetail)
        val text = UnifileBodyText(headingName = headingName, keywords = keywords, summary = summary, detail = detail)
        return Section(heading = heading, text = text)
    }


}
