package com.ingenifi.unifile.model.generators.jira

import com.ingenifi.unifile.model.document.*
import com.ingenifi.unifile.model.generators.KeywordExtractor
import com.ingenifi.unifile.verbosity.VerbosePrinter
import com.ingenifi.unifile.verbosity.VerbosePrinting
import com.ingenifi.unifile.verbosity.Verbosity
import java.util.*
import com.ingenifi.unifile.model.document.TitleText.Title
import com.ingenifi.unifile.model.document.KeywordsText.Keywords
import com.ingenifi.unifile.model.document.DetailText.Detail
import com.ingenifi.unifile.model.document.SummaryText.Summary


class ChildCreatorDelegate(
    private val epicChild: EpicChild, private val keywordExtractor: KeywordExtractor, private val verbosity: Verbosity
) : SectionCreator, VerbosePrinting by VerbosePrinter(verbosity) {

    override fun create(sectionNumber: SectionNumber): List<Section> {
        val reference = epicChild.reference()
        verbosePrint("Processing $reference")
        val headingName = Name("Jira ${epicChild.type.capitalizeWords()}")
        val title = Title(epicChild.title)
        val heading = Heading(headingName = headingName, sectionNumber = sectionNumber, title = title)
        val keywords = createKeywords()
        val detail = Detail(epicChild.detail)
        val summary = Summary( "This is a $reference")
        val text = UnifileBodyText(headingName, keywords = keywords, detail = detail, summary = summary)
        return listOf(Section(heading = heading, text = text))
    }
    
    private fun createKeywords(): Keywords {
        val detailKeywords = keywordExtractor.extract(epicChild.detail)
        val additionalKeywords = mutableListOf<String>()
        additionalKeywords.addAll(epicChild.type.split(" "))
        additionalKeywords.add(epicChild.epic.key)
        additionalKeywords.add(epicChild.key)
        return Keywords(detailKeywords + additionalKeywords)
    }

    private fun String.capitalizeWords(): String = split(Regex("\\b")).joinToString("") {
        if (it.isNotEmpty()) it.replaceFirstChar { char ->
            if (char.isLowerCase()) char.titlecase(Locale.getDefault()) else char.toString()
        } else it
    }
    private fun EpicChild.reference() : String = "$type '$key - $title' from epic '${epic.key} - ${epic.title}'"

}

