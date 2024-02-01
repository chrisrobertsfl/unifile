package com.ingenifi.unifile.model.generators.jira

import com.ingenifi.unifile.VerbosePrinter
import com.ingenifi.unifile.VerbosePrinting
import com.ingenifi.unifile.Verbosity
import com.ingenifi.unifile.model.document.*
import com.ingenifi.unifile.model.document.DetailText.Detail
import com.ingenifi.unifile.model.document.KeywordsText.Keywords
import com.ingenifi.unifile.model.document.SummaryText.Summary
import com.ingenifi.unifile.model.document.TitleText.Title
import com.ingenifi.unifile.model.generators.KeywordExtractor
import java.util.*

data class EpicStoryCreator(val story: EpicStory, val keywordExtractor: KeywordExtractor, val verbosity: Verbosity) : SectionCreator by Delegate(story, keywordExtractor, verbosity)
data class EpicSpikeCreator(val spike: EpicSpike, val keywordExtractor: KeywordExtractor, val verbosity: Verbosity) : SectionCreator by Delegate(spike, keywordExtractor, verbosity)
data class EpicBugCreator(val bug: EpicBug, val keywordExtractor: KeywordExtractor, val verbosity: Verbosity) : SectionCreator by Delegate(bug, keywordExtractor, verbosity)

data class Delegate(val epicChild: EpicChild, val keywordExtractor: KeywordExtractor, val verbosity: Verbosity) : SectionCreator, VerbosePrinting by VerbosePrinter(verbosity) {
    override fun create(sectionNumber: SectionNumber): List<Section> {
        val reference = epicChild.reference()
        verbosePrint("Processing $reference")
        val headingName = Name("Jira ${epicChild.type.capitalizeWords()}")
        val title = Title(epicChild.title)
        val heading = Heading(headingName = headingName, sectionNumber = sectionNumber, title = title)
        val keywords = createKeywords()
        val detail = Detail(epicChild.detail)
        val summary = Summary("This is a $reference")
        val bodyText = UnifileBodyText(headingName, keywords = keywords, detail = detail, summary = summary)
        val section = Section(heading = heading, bodyText = bodyText)
        return listOf(section)
    }

    private fun createKeywords(): Keywords {
        val detailKeywords = keywordExtractor.extract(epicChild.detail)
        val keywords = mutableListOf<String>()
        keywords.addAll(detailKeywords)
        keywords.addAll(epicChild.type.split(" "))
        keywords.add(epicChild.epic.key)
        keywords.add(epicChild.key)
        keywords.addAll(epicChild.epic.keywords)
        return Keywords(keywords)
    }

    private fun String.capitalizeWords(): String = split(Regex("\\b")).joinToString("") {
        if (it.isNotEmpty()) it.replaceFirstChar { char ->
            if (char.isLowerCase()) char.titlecase(Locale.getDefault()) else char.toString()
        } else it
    }

    private fun EpicChild.reference(): String = "$type '$key - $title' from epic '${epic.key} - ${epic.title}'"
}
