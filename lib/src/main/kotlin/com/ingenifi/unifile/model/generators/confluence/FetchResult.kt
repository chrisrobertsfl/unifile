package com.ingenifi.unifile.model.generators.confluence

class FetchResult(private val resultMap: Map<String, Any>) {

    fun get(key: String): Any? = resultMap[key]

    fun getAsString(key: String, trim: Boolean = false) = (get(key) as String).run { if (trim) trim() else this }

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