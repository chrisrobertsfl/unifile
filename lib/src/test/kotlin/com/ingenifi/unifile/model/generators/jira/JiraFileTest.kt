package com.ingenifi.unifile.model.generators.jira

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class JiraFileTest : StringSpec({
    "lines" {
        JiraFile(listOf("OMO-2094")).lines() shouldBe listOf(JiraLine(key = "OMO-2094"))
        JiraFile(listOf("OMO-2094:")).lines() shouldBe listOf(JiraLine(key = "OMO-2094"))
        JiraFile(listOf("OMO-2094:k1")).lines() shouldBe listOf(JiraLine(key = "OMO-2094", listOf("k1")))
        JiraFile(listOf("OMO-2094:k1 ,     k2   ")).lines() shouldBe listOf(JiraLine(key = "OMO-2094", listOf("k1", "k2")))
        JiraFile(listOf("OMO-2094:,")).lines() shouldBe listOf(JiraLine(key = "OMO-2094"))
        JiraFile(listOf("OMO-2094: ,        ")).lines() shouldBe listOf(JiraLine(key = "OMO-2094"))
        JiraFile(listOf("OMO-2094: ,   abc     ")).lines() shouldBe listOf(JiraLine(key = "OMO-2094", listOf("abc")))
    }
})


