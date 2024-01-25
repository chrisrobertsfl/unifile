package com.ingenifi.unifile.formatter.excel

import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileInputStream
import java.io.StringWriter

class ExcelConverter {

    fun convert(excelFile: File): Map<String, WorkSheet> {
        return convertToCsv(excelFile)
    }

    private fun convertToCsv(excelFile: File): Map<String, WorkSheet> {
        FileInputStream(excelFile).use { fis ->
            val workbook = XSSFWorkbook(fis)
            val sheetsData = mutableMapOf<String, WorkSheet>()
            val workbookName = excelFile.name

            for (sheetIndex in 0 until workbook.numberOfSheets) {
                val sheet = workbook.getSheetAt(sheetIndex)
                var isFirstRow = true
                var headers: List<String> = listOf()
                val bodyWriter = StringWriter()

                sheet.forEach { row ->
                    val rowValues = row.map { cell ->
                        when (cell.cellType) {
                            CellType.STRING -> cell.stringCellValue
                            CellType.NUMERIC -> cell.numericCellValue.toString()
                            CellType.BOOLEAN -> cell.booleanCellValue.toString()
                            else -> ""
                        }.replace("\"", "\"\"") // Escape double quotes
                    }

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

                sheetsData[sheet.sheetName] = WorkSheet(workbookName, sheet.sheetName, headers, bodyWriter.toString())
            }

            workbook.close()
            return sheetsData
        }
    }
}
