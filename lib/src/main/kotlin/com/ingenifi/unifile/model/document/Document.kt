package com.ingenifi.unifile.model.document

data class Document(val tableOfContents : TableOfContents, val body : Body)
data class TableOfContents(val header : String = "Table Of Contents", val headings : List<Heading>)
data class Body(val sections : List<Section>)
data class Section(val heading : Heading, val text : Text)
data class Heading(val sectionNumber: SectionNumber, val title: Title)
data class SectionNumber(val levels : List<Level>)
data class Level(val number : Int)
data class Title(val content : String)
data class Text(val content : String)

