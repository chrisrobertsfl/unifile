package com.ingenifi.unifile.formatter.excel


import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.xssf.usermodel.XSSFSheet
import java.io.StringWriter

class SheetProcessor(private val sheet: XSSFSheet, private val workbookName: String) {
    fun process(): WorkSheet {
        var isFirstRow = true
        var headers: List<String> = listOf()
        val bodyWriter = StringWriter()

        sheet.forEach { row ->
            val rowValues = processRow(row)

            if (rowValues.any { it.isNotEmpty() }) {
                val csvRow = rowValues.joinToString(",") { "\"$it\"" }

                if (isFirstRow) {
                    headers = rowValues
                    isFirstRow = false
                } else {
                    bodyWriter.write("$csvRow\n")
                }
            }
        }

        return WorkSheet(workbookName, sheet.sheetName, headers, bodyWriter.toString())
    }

    private fun processRow(row: Row): List<String> {
        return row.map { cell ->
            when (cell.cellType) {
                CellType.STRING -> cell.stringCellValue
                CellType.NUMERIC -> cell.numericCellValue.toString()
                CellType.BOOLEAN -> cell.booleanCellValue.toString()
                else -> ""
            }.replace("\"", "\"\"") // Escape double quotes
        }
    }
}
