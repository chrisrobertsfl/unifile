package com.ingenifi.unifile

import com.google.common.io.Resources
import com.spire.doc.Document
import com.spire.doc.FileFormat
import com.spire.pdf.PdfDocument
import com.spire.presentation.Presentation
import com.spire.xls.Workbook
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.apache.pdfbox.io.RandomAccessReadBuffer
import org.apache.pdfbox.pdfparser.PDFParser
import org.apache.pdfbox.text.PDFTextStripper
import org.slf4j.LoggerFactory
import picocli.CommandLine
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.InputStream
import java.nio.charset.StandardCharsets
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.Callable


@CommandLine.Command(
    name = "unifile", mixinStandardHelpOptions = true, description = ["Utility to process and combine files."]
)
class UniFileCli : Callable<Int> {

    private val logger = LoggerFactory.getLogger(UniFileCli::class.java)

    @CommandLine.Option(names = ["-i", "--input"], description = ["Input files or directories"])
    private var inputPaths: Array<String> = arrayOf()


    @CommandLine.Option(names = ["-o", "--output"], description = ["Output file path"])
    private var outputPath: String? = null


    @CommandLine.Option(names = ["-v", "--verbose"], description = ["Enable verbose output"])
    private var verbose: Boolean = false

    override fun call(): Int {
        if (inputPaths.isEmpty()) {
            CommandLine.usage(this, System.out)
            return 1
        }
        return try {
            val output = OutputPath.from(outputPath)
            val input = InputPaths(inputPaths.toList())
            UniFile(input).combineFiles(output)
            if (output is FileOutputPath) {
                logger.info("Combined file created: {}", output.path)
            }
            0
        } catch (e: Exception) {
            logger.error("Failed to process files: {}", e.message, e)
            1
        }
    }
}

data class UniFile(val input: InputPaths, val maxFileSizeMB: Int = 19, val ejectBlankLines: Boolean = true) {
    fun combineFiles(output: OutputPath) {
        val stopWords: List<String> = Resources.toString(Resources.getResource("all-stop-words.txt"), StandardCharsets.UTF_8).lines()
        val keywordExtractor = KeywordExtractor(stopWords = stopWords)
        val contents = Contents()
        input.list.flatMap { it.findFiles() }.map {
            ContentConverter.from(it.extension, keywordExtractor).convert(it)
        }.forEach { contents.add(it) }
        output.write(contents.toJsonString())
    }

}

sealed interface ContentConverter {

    fun convert(file: File): Content

    companion object {
        fun from(extension: String, keywordExtractor: KeywordExtractor): ContentConverter = when (extension.lowercase()) {
            "doc" -> Word(keywordExtractor, ContentType.DOC)
            "docx" -> Word(keywordExtractor, ContentType.DOCX)
            "pdf" -> Pdf(keywordExtractor)
            "ppt" -> PowerPoint(keywordExtractor, ContentType.PPT)
            "pptx" -> PowerPoint(keywordExtractor, ContentType.PPTX)
            "txt" -> PlainText(keywordExtractor)
            "xls" -> Excel(keywordExtractor, ContentType.XLS)
            "xlsx" -> Excel(keywordExtractor, ContentType.XLSX)

            else -> Unknown
        }
        val TEXT_STRIPPER = PDFTextStripper()
    }


    fun File.keywords(): List<String> = name.substringBeforeLast(".").split(Regex("\\W+")).filter { it.isNotBlank() }


    fun File.bodyFromPdf(inputStream : InputStream) : String = RandomAccessReadBuffer(inputStream).use {
        TEXT_STRIPPER.getText(PDFParser(it).parse())
    }
    data class Word(private val keywordExtractor: KeywordExtractor, private val type : ContentType) : ContentConverter {
        override fun convert(file: File): Content {

            val body = file.bodyFromPdf(file.asStream())
            return Content(type = type, source = file.name, keywords = keywordExtractor.extract(body) + file.keywords(), body = body)
        }

        private fun File.asStream(): ByteArrayInputStream {
            val document = Document()
            document.loadFromFile(absolutePath)
            val bos = ByteArrayOutputStream()
            document.saveToStream(bos, FileFormat.PDF)
            return ByteArrayInputStream(bos.toByteArray())
        }
    }

    data class PowerPoint(private val keywordExtractor: KeywordExtractor, private val type : ContentType) : ContentConverter {
        override fun convert(file: File): Content {

            val body = file.bodyFromPdf(file.asStream())
            return Content(type = type, source = file.name, keywords = keywordExtractor.extract(body) + file.keywords(), body = body)
        }

        private fun File.asStream(): ByteArrayInputStream {
            val presentation = Presentation()
            presentation.loadFromFile(absolutePath)
            val bos = ByteArrayOutputStream()
            try {
                presentation.saveToFile(bos, com.spire.presentation.FileFormat.PDF)
            } catch( e : Exception) {
                println("Finally caught npe: ${e.message}")
            }
            return ByteArrayInputStream(bos.toByteArray())
        }
    }

    data class Excel(private val keywordExtractor: KeywordExtractor, private val type : ContentType) : ContentConverter {
        override fun convert(file: File): Content {

            val body = file.bodyFromPdf(file.asStream())
            return Content(type = type, source = file.name, keywords = keywordExtractor.extract(body) + file.keywords(), body = body)
        }

        private fun File.asStream(): ByteArrayInputStream {
            val workbook = Workbook()
            workbook.loadFromFile(absolutePath)
            val bos = ByteArrayOutputStream()
            workbook.saveToStream(bos, com.spire.xls.FileFormat.PDF)
            return ByteArrayInputStream(bos.toByteArray())
        }
    }
    data class Pdf(private val keywordExtractor: KeywordExtractor) : ContentConverter {
        override fun convert(file: File): Content {
            val body = file.bodyFromPdf(file.asStream())
            return Content(type = ContentType.PDF, source = file.name, keywords = keywordExtractor.extract(body) + file.keywords(), body = body)
        }

        private fun File.asStream(): ByteArrayInputStream {
            val pdf = PdfDocument()
            pdf.loadFromFile(absolutePath)
            val bos = ByteArrayOutputStream()
            pdf.saveToStream(bos, com.spire.pdf.FileFormat.PDF)
            return ByteArrayInputStream(bos.toByteArray())
        }
    }

    data class PlainText(private val keywordExtractor: KeywordExtractor) : ContentConverter {
        override fun convert(file: File): Content {
            val body = file.readText()
            return Content(type = ContentType.PLAIN_TEXT, source = file.name, keywords = keywordExtractor.extract(body) + file.keywords(), body = body)
        }
    }

    object Unknown : ContentConverter {
        override fun convert(file: File): Content = throw UnsupportedOperationException("No converter associate with path '${file.absolutePath}'")

    }
}

@Serializable
enum class ContentType {

    @SerialName("word document .doc")
    DOC,

    @SerialName("word document .docx")
    DOCX,

    @SerialName("pdf document")
    PDF,

    @SerialName("plain text document")
    PLAIN_TEXT,

    @SerialName("powerpoint presentation document .ppt")
    PPT,

    @SerialName("powerpoint presentation document .pptx")
    PPTX,

    @SerialName("excel document .xls")
    XLS,

    @SerialName("excel document .xlsx")
    XLSX,

    @SerialName("html document")
    HTML,

    // Add other content types here with their respective serialized names
}

@Serializable
data class Content(val type: ContentType, val source: String, val keywords: List<String>, val body: String)

@Serializable
data class Contents(val contents: MutableList<Content> = mutableListOf()) {
    fun toJsonString(): String {
        val prettyJson = Json { prettyPrint = true }
        return prettyJson.encodeToString(serializer(), this)
    }

    fun add(content: Content) {
        contents.add(content)
    }

}

class InputPaths(paths: List<String>) {
    val list: List<InputPath> = paths.map { InputPath(it) }
}

class InputPath(private val pathName: String) {
    fun findFiles(): List<File> = when {
        pathName == "." -> File(".").walkTopDown().filter { it.isFile }.toList()
        File(pathName).isDirectory -> File(pathName).walkTopDown().filter { it.isFile }.toList()
        else -> listOf(File(pathName))
    }
}


sealed interface OutputPath {

    companion object {
        fun from(pathName: String?): OutputPath = if (pathName == null) ConsoleOutputPath else FileOutputPath(pathName)
    }

    fun write(text: String)

    object ConsoleOutputPath : OutputPath {
        override fun write(text: String) {
            println(text)
        }
    }
}


data class FileOutputPath(var path: String?) : OutputPath {
    init {
        if (path.isNullOrEmpty()) {
            path = generatePathName()
        }
    }

    override fun write(text: String) {
        File(path!!).apply {
            parentFile?.mkdirs()  // Ensure the directory exists
            // Change to appendText to add to the file rather than overwrite it
            appendText(text)
        }
    }

    private fun generatePathName(): String {
        return "unifile-${LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MMM-dd-HH:mm:ss"))}.txt"
    }
}
