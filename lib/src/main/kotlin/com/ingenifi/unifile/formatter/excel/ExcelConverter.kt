package com.ingenifi.unifile.formatter.excel

import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileInputStream
import java.io.StringWriter

class ExcelConverter {

    fun convert(excelFile: File): Map<String, String> {
        return convertToCsv(excelFile)
    }

    private fun convertToCsv(excelFile: File): Map<String, String> {
        FileInputStream(excelFile).use { fis ->
            val workbook = XSSFWorkbook(fis)
            val sheetsData = mutableMapOf<String, String>()

            for (sheetIndex in 0 until workbook.numberOfSheets) {
                val sheet = workbook.getSheetAt(sheetIndex)
                val stringWriter = StringWriter()

                sheet.forEach { row ->
                    val rowValues = row.map { cell ->
                        when (cell.cellType) {
                            CellType.STRING -> cell.stringCellValue
                            CellType.NUMERIC -> cell.numericCellValue.toString()
                            CellType.BOOLEAN -> cell.booleanCellValue.toString()
                            else -> ""
                        }.replace("\"", "\"\"") // Escape double quotes
                    }

                    // Skip rows where all cells are empty
                    if (rowValues.any { it.isNotEmpty() }) {
                        val csvRow = rowValues.joinToString(",") { "\"$it\"" } // Quote each field
                        stringWriter.write("$csvRow\n")
                    }
                }

                sheetsData[sheet.sheetName] = stringWriter.toString()
            }

            workbook.close()
            return sheetsData
        }
    }
}
