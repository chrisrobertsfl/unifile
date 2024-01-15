package com.ingenifi.unifile

import com.ingenifi.unifile.content.KeywordExtractor
import io.kotest.core.annotation.Ignored
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe


@Ignored
class KeywordExtractorSpec : StringSpec({
    "extract" {
        val extractor = KeywordExtractor(.2, stopWords = listOf())
        val text = """
            Once upon a time, in a land far away, there lived a wise old owl who, perched atop the tallest oak tree in the ancient forest that was home to creatures of various kinds – from the tiny, scurrying field mice who played hide and seek in the underbrush, darting in and out of their burrows, to the majestic deer who roamed the meadows gracefully, their antlers reaching up like branches towards the sunlit sky, a sky that changed colors as the day progressed, from the pale blue of dawn to the bright azure of midday and then to the fiery oranges and pinks of sunset, a spectacle that never failed to amaze the inhabitants of the forest, each of whom contributed in their own unique way to the life of the forest, such as the busy beavers, always at work on their dams, skillfully using their sharp teeth to cut through wood, the birds that filled the air with their melodious songs, each note a testament to the beauty of the natural world, the squirrels, ever playful and mischievous, chasing each other up and down the trunks of trees, their bushy tails flicking back and forth, the wise old tortoises who moved slowly but with purpose, their shells a map of the many years they had lived, each line telling a story of seasons gone by, the frogs that croaked melodiously by the pond at night, their chorus a familiar lullaby to the creatures of the forest, and in the heart of the forest, there flowed a clear, bubbling stream, its waters fresh and cool, a source of life and joy for all who lived there, the stream meandering through the forest, winding its way past rocks and fallen logs, beneath overhanging branches that dipped their leaves into the flowing water, creating ripples that glided across the surface, the water reflecting the ever-changing sky above, and among these wonders, the wise old owl watched over the forest, his eyes sharp and knowing, having seen many generations of creatures come and go, each leaving their mark on the forest, contributing to the tapestry of life that was woven through the trees, the air, the very soil of the forest, a tapestry rich with stories, with memories, with the essence of the forest itself, a place of magic and wonder, of peace and of wildness, a place where every creature, no matter how small, had a role to play, a place that was constantly evolving, growing, changing, yet always remaining, in its essence, the same – a haven for all who dwelled within its embrace, a haven that the wise old owl, in his wisdom, knew was precious, a treasure to be protected, cherished, and celebrated, for in this forest, in the harmony of its many, varied inhabitants, lay the true magic of life, a magic that was as old as time itself, a magic that would continue to thrive as long as there were those who believed in it, who respected it, who loved it, and so, from his perch high above, the wise old owl continued to watch, to listen, to learn, his heart full of the stories of the forest, stories he would carry with him always, for as long as the winds whispered through the leaves, as long as the stream flowed, as long as the stars shone down upon the forest, the story of this magical place would live on, a story of life, of beauty, of interconnectedness, a story that was never-ending, ever-unfolding, just like the forest itself, a story that was, in every sense, the story of the world.
        """.trimIndent()
        extractor.extract(text).size shouldBe 24
    }
}) {
}