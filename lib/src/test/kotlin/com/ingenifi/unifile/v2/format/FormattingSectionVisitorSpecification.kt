package com.ingenifi.unifile.v2.format

import com.ingenifi.unifile.v2.model.*
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk

class FormattingSectionVisitorSpecification : FeatureSpec({
    feature("text file") {
        scenario("visit using date from file system") {
            val id = Id(1)
            val title = Title("title")
            val keywords = Keywords(list = listOf("keyword1", "keyword2"))
            val lastUpdated = mockk<LastUpdated>()
            every { lastUpdated.text } returns "2024-02-20"
            val summary = Summary(text = "summary")
            val content = Content(text = "content")
            val relatedSections = RelatedSections(list = listOf(Id(2), Id(3)))
            val metadata = SectionMetadata(keywords = keywords, lastUpdated = lastUpdated, summary = summary)
            val section = Section(id, title, metadata, content, relatedSections)
            val visitor = FormattingSectionVisitor()
            section.accept(visitor)
            visitor.getFormattedText() shouldBe """
                <!-- Section ID: S0001 -->
                ## title

                **Metdata:**
                - **Tags/Keywords**: [keyword1, keyword2]
                - **Summary**: summary
                - **Last Updated**: 2024-02-20

                **Content:**
                content

                **Related Sections:** [S0002, S0003]
                
            """.trimIndent()
        }
    }

})

