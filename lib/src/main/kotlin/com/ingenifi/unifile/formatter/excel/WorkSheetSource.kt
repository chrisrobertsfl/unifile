package com.ingenifi.unifile.formatter.excel

import com.ingenifi.unifile.formatter.EntrySource
import com.ingenifi.unifile.formatter.toc.HeadingNumber

data class WorkSheetSource(val worksheet: WorkSheet, override val headingNumber: HeadingNumber) : EntrySource {
    override fun description(): String = worksheet.toFullCsv()
    override fun title(): String = worksheet.title

    fun header() = worksheet.header
    fun workbookName() : String = worksheet.workbookName
}