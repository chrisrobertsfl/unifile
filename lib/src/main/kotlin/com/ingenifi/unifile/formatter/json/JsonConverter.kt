package com.ingenifi.unifile.formatter.json

import kotlinx.serialization.json.*

class JsonConverter {

    fun convert(json: String): String {
        val jsonElement = Json.parseToJsonElement(json)
        return buildString { parseJsonElement(jsonElement, 0, this) }
    }

    private fun parseJsonElement(element: JsonElement, indent: Int, builder: StringBuilder) {
        when {
            element is JsonObject -> {
                element.forEach { (key, value) ->
                    if (!value.isNull() && !(value is JsonPrimitive && value.content.isBlank())) {
                        builder.append("  ".repeat(indent)).append(key).append(":\n")
                        parseJsonElement(value, indent + 1, builder)
                    }
                }
            }
            element is JsonArray -> {
                element.forEach { arrayElement ->
                    if (!arrayElement.isNull() && !(arrayElement is JsonPrimitive && arrayElement.content.isBlank())) {
                        parseJsonElement(arrayElement, indent, builder)
                    }
                }
            }
            element is JsonPrimitive && !element.content.isBlank() -> {
                builder.append("  ".repeat(indent)).append(element.content).append("\n")
            }
            // JsonNull and blank JsonPrimitives are ignored
        }
    }

    private fun JsonElement.isNull(): Boolean = this is JsonNull
}
