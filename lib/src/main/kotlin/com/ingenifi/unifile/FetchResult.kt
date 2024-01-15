package com.ingenifi.unifile

class FetchResult(private val resultMap: Map<String, Any>) {

    fun get(key: String): Any? = resultMap[key]

    fun get(): Any? = resultMap["payload"]

    fun getTokens(key: String): List<String>? {
        return when (val value = get(key)) {
            is String -> tokenize(value)
            is List<*> -> value.filterIsInstance<String>().flatMap { tokenize(it) }
            else -> null
        }
    }

    private fun tokenize(str: String): List<String> {
        return str.split("\\s+".toRegex())
    }
}