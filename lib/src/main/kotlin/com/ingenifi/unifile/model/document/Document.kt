package com.ingenifi.unifile.model.document

data class Document(val tableOfContents : TableOfContents, val body : Body) {
    constructor(sections : List<Section>) : this(tableOfContents = TableOfContents(headings = sections.map { it.heading }), body = Body(sections))
}
data class TableOfContents(val header : String = "Table Of Contents", val headings : List<Heading>)
data class Body(val sections : List<Section>)
data class Section(val heading : Heading, val text : BodyText)
data class Heading(val headingName : HeadingName = HeadingName.None, val sectionNumber: SectionNumber, val title: Title)
 sealed interface HeadingName {
     val content : String

     object None : HeadingName {
         override val content: String get() = ""
     }
}
data class Name(override val content : String) : HeadingName

data class SectionNumber(val levels : List<Level>)
data class Level(val number : Int)
data class Title(val content : String)
interface BodyText {
    val content : String
}
data class Text(override val content : String) : BodyText

