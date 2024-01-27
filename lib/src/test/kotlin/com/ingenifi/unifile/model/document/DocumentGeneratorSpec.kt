package com.ingenifi.unifile.model.document

import com.ingenifi.unifile.model.generators.document.DocumentGenerator
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk

class DocumentGeneratorSpec : FeatureSpec({
    feature("table of contents") {
        val body = mockk<Body>()
        every { body.sections } returns listOf()
        val document = Document(
            tableOfContents = TableOfContents(
                headings = listOf(
                    Heading(sectionNumber = SectionNumber(levels = listOf(Level(1))), headingName = Name("Epic"), title = Title("My first title")),
                    Heading(sectionNumber = SectionNumber(levels = listOf(Level(1), Level(1))), headingName = Name("Epic Child Story"), title = Title("My first sub title")),
                    Heading(sectionNumber = SectionNumber(levels = listOf(Level(2))), headingName = Name("Document"), title = Title("My second title")),
                    Heading(sectionNumber = SectionNumber(levels = listOf(Level(2), Level(1))), headingName = Name("Section"), title = Title("My second sub title")),
                    Heading(sectionNumber = SectionNumber(levels = listOf(Level(2), Level(1), Level(1))), headingName = Name("Subsection"), title = Title("My second sub sub title")),
                )
            ), body = body
        )

        scenario("generate with no justification") {
            DocumentGenerator(document).generate() shouldBe """
            ===========================================
            Table Of Contents
            ===========================================
            1. Epic - My first title
            1.1. Epic Child Story - My first sub title
            2. Document - My second title
            2.1. Section - My second sub title
            2.1.1. Subsection - My second sub sub title
        """.trimIndent()
        }

        scenario("generate with justification") {
            DocumentGenerator(document = document, justifySectionNumbers = true).generate() shouldBe """
            ============================================
            Table Of Contents
            ============================================
                1. Epic - My first title
              1.1. Epic Child Story - My first sub title
                2. Document - My second title
              2.1. Section - My second sub title
            2.1.1. Subsection - My second sub sub title
        """.trimIndent()
        }
    }
    feature("body") {
        scenario("simple") {
            val tableOfContents = mockk<TableOfContents>()
            every { tableOfContents.header } returns ""
            every { tableOfContents.headings } returns listOf()
            val document = Document(
                tableOfContents = tableOfContents, body = Body(
                    sections = listOf(
                        Section(heading = Heading(sectionNumber = SectionNumber(levels = listOf(Level(1))), headingName = Name("Document"), title = Title("My first title")), text = Text("My first text is very long but is not considered in calculating the max border length for section headings")),
                        Section(heading = Heading(sectionNumber = SectionNumber(levels = listOf(Level(2))), headingName = Name("PDF"), title = Title("My second title")), text = Text("My second text"))

                    )
                )
            )
            DocumentGenerator(document).generate() shouldBe """
                ============================
                1. Document - My first title
                ============================
                My first text is very long but is not considered in calculating the max border length for section headings
                
                ============================
                2. PDF - My second title
                ============================
                My second text
            """.trimIndent()
        }
    }

    feature("document") {
        scenario("Entire document is generated with justification") {
            val body = Body(
                sections = listOf(
                    Section(heading = Heading(sectionNumber = SectionNumber(levels = listOf(Level(1))), title = Title("My first title")), text = Text("My first text is very long but is not considered in calculating the max border length for section headings")),
                    Section(heading = Heading(sectionNumber = SectionNumber(levels = listOf(Level(2))), title = Title("My second title")), text = Text("My second text"))

                )
            )
            val document = Document(
                tableOfContents = TableOfContents(
                    headings = listOf(
                        Heading(sectionNumber = SectionNumber(levels = listOf(Level(1))), title = Title("My first title")),
                        Heading(sectionNumber = SectionNumber(levels = listOf(Level(1), Level(1))), title = Title("My first sub title")),
                        Heading(sectionNumber = SectionNumber(levels = listOf(Level(2))), title = Title("My second title")),
                        Heading(sectionNumber = SectionNumber(levels = listOf(Level(2), Level(1))), title = Title("My second sub title")),
                        Heading(sectionNumber = SectionNumber(levels = listOf(Level(2), Level(1), Level(1))), title = Title("My second sub sub title")),
                    )
                ), body = body
            )

            DocumentGenerator(document = document, justifySectionNumbers = true).generate() shouldBe """
            ==============================
            Table Of Contents
            ==============================
                1. My first title
              1.1. My first sub title
                2. My second title
              2.1. My second sub title
            2.1.1. My second sub sub title
            
            
            ==============================
                1. My first title
            ==============================
            My first text is very long but is not considered in calculating the max border length for section headings
            
            ==============================
                2. My second title
            ==============================
            My second text
        """.trimIndent()
        }
    }
})

