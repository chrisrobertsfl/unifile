package com.ingenifi.unifile.formatter.excel

import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileInputStream
import java.io.StringWriter

class ExcelConverter {

    fun convert(excelFile: File): String {
        return convertToCsv(excelFile)
    }

    private fun convertToCsv(excelFile: File): String {
        FileInputStream(excelFile).use { fis ->
            val workbook = XSSFWorkbook(fis)
            val sheet = workbook.getSheetAt(0) // Assuming the first sheet
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

            workbook.close()
            return stringWriter.toString()
        }
    }
}
