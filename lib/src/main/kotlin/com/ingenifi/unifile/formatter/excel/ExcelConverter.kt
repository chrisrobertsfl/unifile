package com.ingenifi.unifile.formatter.excel

import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileInputStream

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
                val sheetProcessor = SheetProcessor(sheet, workbookName)
                sheetsData[sheet.sheetName] = sheetProcessor.process()
            }

            workbook.close()
            return sheetsData
        }
    }
}
