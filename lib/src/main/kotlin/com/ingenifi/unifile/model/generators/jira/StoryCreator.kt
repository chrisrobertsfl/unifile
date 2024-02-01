package com.ingenifi.unifile.model.generators.jira

import com.ingenifi.unifile.model.document.*
import com.ingenifi.unifile.model.generators.KeywordExtractor
import com.ingenifi.unifile.VerbosePrinter
import com.ingenifi.unifile.VerbosePrinting
import com.ingenifi.unifile.Verbosity

data class StoryCreator(val story: Story, val keywordExtractor: KeywordExtractor, val verbosity: Verbosity) : SectionCreator, VerbosePrinting by VerbosePrinter(verbosity) {
    override fun create(sectionNumber: SectionNumber): List<Section> {
        verbosePrint("Processing Story '${story.key} - ${story.title}'")
        val headingName = Name("Jira Story")
        val title = TitleText.Title(story.title)
        val heading = Heading(headingName = headingName, sectionNumber = sectionNumber, title = title)
        val keywords = KeywordsText.Keywords(keywordExtractor.extract(story.detail))
        val text = UnifileBodyText(headingName, keywords = keywords, detail = DetailText.Detail(story.detail))
        val section = Section(heading = heading, bodyText = text)
        return listOf(section)
    }
}