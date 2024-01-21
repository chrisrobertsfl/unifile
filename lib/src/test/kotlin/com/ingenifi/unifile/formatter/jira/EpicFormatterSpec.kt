package com.ingenifi.unifile.formatter.jira

import com.ingenifi.unifile.Verbosity
import com.ingenifi.unifile.formatter.KeywordExtractor
import com.ingenifi.unifile.formatter.toc.TableOfContents
import io.kotest.core.spec.style.StringSpec

class EpicFormatterSpec : StringSpec({

    "format epic" {

        val formatter = EpicFormatter(
            epic = Epic(
                key = "OMO-1234", title = "This is a test title", introduction = "this is some kinda intro", description = "a very short description", children = mutableListOf(
                    Story(key = "OMO-1235", title = "this is a child story title", description = "story description")
                )
            ), keywordExtractor = KeywordExtractor(), verbosity = Verbosity.NONE, toc = TableOfContents()
        )
        val output = formatter.format(1)
        println(formatter.toc.format())
        println(output)
    }

    "format story" {
        val formatter = StoryFormatter(
            story = Story(key = "OMO-1234", title = "This is a test title", description = "a very short description"),
            keywordExtractor = KeywordExtractor(),
            verbosity = Verbosity.NONE,
            toc = TableOfContents()
        )
        val output = formatter.format(1)
        println(formatter.toc.format())
        println(output)
    }

    "format spike" {
        val formatter = SpikeFormatter(
            spike = Spike(key = "OMO-1234", title = "This is a test title", description = "a very short description"),
            keywordExtractor = KeywordExtractor(),
            verbosity = Verbosity.NONE,
            toc = TableOfContents()
        )
        val output = formatter.format(1)
        println(formatter.toc.format())
        println(output)
    }


    "format epic story" {
        val epic = Epic(
            key = "OMO-1234", title = "This is a test title", introduction = "this is some kinda intro", description = "a very short description", children = mutableListOf(
                Story(key = "OMO-1235", title = "this is a child story title", description = "story description")
            )
        )
        val formatter = EpicStoryFormatter(
            epic = epic, keywordExtractor = KeywordExtractor(), verbosity = Verbosity.NONE, toc = TableOfContents(), childNumber = 1, story = epic.children.first() as Story
        )
        val output = formatter.format(1)
        println(formatter.toc.format())
        println(output)
    }

    "format epic spike" {
        val epic = Epic(
            key = "OMO-1234", title = "This is a test title", introduction = "this is some kinda intro", description = "a very short description", children = mutableListOf(
                Spike(key = "OMO-1235", title = "this is a child spike title", description = "story description")
            )
        )
        val formatter = EpicSpikeFormatter(
            epic = epic, keywordExtractor = KeywordExtractor(), verbosity = Verbosity.NONE, toc = TableOfContents(), childNumber = 1, spike = epic.children.first() as Spike
        )
        val output = formatter.format(1)
        println(formatter.toc.format())
        println(output)
    }

})

