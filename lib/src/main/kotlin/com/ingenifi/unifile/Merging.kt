package com.ingenifi.unifile

import com.spire.pdf.PdfDocument
import java.io.FileOutputStream
import java.io.OutputStream
import java.nio.file.Paths


sealed interface OutputMerger<T> {
    fun merge(outputs: List<Output>): T
    fun merge(vararg outputs: Output) = merge(outputs.toList())
}

data class UnifyingMerger(private val separator: String = "\n--------------------------\n") : OutputMerger<String> {
    override fun merge(outputs: List<Output>): String {
        val pdfOutput = PdfMerger.merge(outputs)
        return TextMerger(separator).merge(outputs + pdfOutput)
    }
}

object PdfMerger : OutputMerger<Output> {
    override fun merge(outputs: List<Output>): Output {
        val inputStreams = outputs.filterIsInstance<PdfStreamOutput>().map { it.asStream() }.toTypedArray()
        if (inputStreams.isEmpty()) return Output.NoopOutput
        val pdf = PdfDocument.mergeFiles(inputStreams)
        val outputPdf = "/tmp/text.pdf"
        val outputStream: OutputStream = FileOutputStream(outputPdf) // TODO:  Generate this
        pdf.save(outputStream)
        pdf.close()
        return  PdfOutput(Paths.get(outputPdf))
    }
}
data class TextMerger(private val separator: String = "\n---\n") : OutputMerger<String> {
    override fun merge(outputs: List<Output>): String = outputs.filterIsInstance<TextOutput>().joinToString(separator) { format(it) }

    fun format(output : TextOutput): String = buildString {
        append("File: ${output.fullPath}\n")
        append("Contents:\n")
        append("${output.asText()}")
    }

}


