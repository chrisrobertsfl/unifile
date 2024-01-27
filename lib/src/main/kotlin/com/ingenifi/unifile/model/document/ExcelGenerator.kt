package com.ingenifi.unifile.model.document

import com.ingenifi.unifile.formatter.excel.ExcelConverter
import com.ingenifi.unifile.formatter.excel.WorkSheet
import com.ingenifi.unifile.model.document.DetailText.Detail
import com.ingenifi.unifile.model.document.KeywordsText.Keywords
import com.ingenifi.unifile.model.document.SummaryText.Summary
import java.io.File

data class ExcelGenerator(val config: SectionGenerator.Config, val number: Int, val file: File, val headingNameString: String = "Excel Document") : SectionGenerator {
    private val fileKeywords = config.keywordExtractor.extractKeywords(file)
    override fun generate(): List<Section> {
        val worksheetData = ExcelConverter().convert(file)
        val workbookSection = generateWorkbookSection(worksheetData.keys)
        val worksheetSections = worksheetData.values.mapIndexed { index, worksheet ->
            generateWorksheetSection(worksheet, number + index)
        }
        return listOf(workbookSection) + worksheetSections
    }


    private fun generateWorkbookSection(worksheetNames: Set<String>): Section {
        fun createTitle() = file.name.substringBeforeLast(".xls")
        fun Set<String>.asWorksheetList(): String = joinToString(separator = ", ", prefix = "'", postfix = "'") { it }
        fun createHeading(headingName: Name, title: String) = Heading(headingName = headingName, sectionNumber = SectionNumber(listOf(Level(number))), title = Title(title))
        fun createKeywords(worksheetNames: Set<String>) = Keywords(fileKeywords + worksheetNames)
        fun createSummary(title: String, worksheetNames: Set<String>) =
            Summary("This is the workbook '$title' containing ${worksheetNames.size} worksheet(s) named: ${worksheetNames.asWorksheetList()}")
        fun createDetail(title: String) = Detail("Subsections represent worksheets contained in this workbook '$title'")

        val headingName = Name("Excel Workbook")
        val title = createTitle()
        val heading = createHeading(headingName, title)
        val keywords = createKeywords(worksheetNames)
        val summary = createSummary(title, worksheetNames)
        val detail = createDetail(title)
        val text = createBodyText(headingName, keywords, summary, detail)
        return Section(heading = heading, text = text)
    }

    private fun generateWorksheetSection(worksheet: WorkSheet, sectionNumberCounter: Int): Section {
        fun createSummary(title: String, worksheet: WorkSheet) = Summary("This is worksheet '$title' of workbook '${worksheet.workbookName}")
        fun createHeading(headingName: Name, sectionNumberCounter: Int, title: String) = Heading(headingName, SectionNumber(listOf(Level(number), Level(sectionNumberCounter))), Title(title))
        fun createKeywords(worksheet: WorkSheet, detail: String): KeywordsText = Keywords(config.keywordExtractor.extract(detail) + fileKeywords + worksheet.title + worksheet.workbookName)

        val headingName = Name("Excel Worksheet")
        val title = worksheet.title
        val heading = createHeading(headingName, sectionNumberCounter, title)
        val csvDetail = worksheet.toFullCsv()
        val keywords = createKeywords(worksheet, csvDetail)
        val summary = createSummary(title, worksheet)
        val detail = Detail(csvDetail)
        val text = createBodyText(headingName, keywords, summary, detail)
        return Section(heading = heading, text = text)
    }
    private fun createBodyText(headingName: Name, keywords: KeywordsText, summary: Summary, detail: Detail) = UnifileBodyText(headingName, keywords, summary, detail)
}