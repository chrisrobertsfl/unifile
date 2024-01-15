package com.ingenifi.unifile.content

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class ContentType {

    @SerialName("confluence link")
    CONFLUENCE_LINK,

    @SerialName("word document .doc")
    DOC,

    @SerialName("word document .docx")
    DOCX,

    @SerialName("json")
    JSON,

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