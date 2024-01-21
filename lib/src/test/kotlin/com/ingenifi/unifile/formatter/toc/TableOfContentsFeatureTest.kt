package com.ingenifi.unifile.formatter.toc

import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe

class TableOfContentsFeatureTest : FeatureSpec({

    feature("Section Sorting") {
        scenario("should sort section numbers correctly") {
            val unsortedSections = listOf(
                SectionNumber(1, 1), SectionNumber(1), SectionNumber(2, 1), SectionNumber(1, 1, 1), SectionNumber(2), SectionNumber(3, 1), SectionNumber(3, 10), SectionNumber(3, 2)
            )
            val sectionSorter = SectionSorter(unsortedSections)
            val sortedSections = sectionSorter.sortSections()

            sortedSections shouldBe listOf(
                SectionNumber(1), SectionNumber(1, 1), SectionNumber(1, 1, 1), SectionNumber(2), SectionNumber(2, 1), SectionNumber(3, 1), SectionNumber(3, 2), SectionNumber(3, 10)
            )
        }
    }

    feature("TOC Formatting") {
        scenario("formatted table of contents should have equal length lines") {
            val toc = TableOfContents(
                mutableListOf(
                    Entry(SectionNumber(1), "Introduction"), Entry(SectionNumber(1, 1), "Getting Started"), Entry(SectionNumber(3, 10), "Advanced Topics")
                )
            )
            val formatter = JustifiedTableOfContentsFormatter()
            val formattedTOC = toc.format(formatter)
            println(formattedTOC)

            val lines = formattedTOC.split("\n")
            val expectedLength = lines.maxOf { it.length }
            lines.forEach { line ->
                line.length shouldBe expectedLength
            }
        }
    }

    feature("TOC Header Formatting") {
        scenario("header should be centered and wrapped if too long") {
            val longTitle = "This is a Very Long Table of Contents Title That Should Wrap"
            val header = Header(title = longTitle)
            val formatter = JustifiedTableOfContentsFormatter(header = header)
            val toc = TableOfContents(
                mutableListOf(
                    Entry(SectionNumber(1, 1), "Sub introduction"),
                    Entry(SectionNumber(1), "Introduction"),
                )
            )

            val formattedTOC = toc.format(formatter)
            println(formattedTOC)

            // Check that the header is centered and wrapped correctly
            // Assertions will be based on the expected format
        }
    }

})