package com.ingenifi.unifile

import org.slf4j.Logger
import java.io.File
import java.util.Properties

data class ParameterStore(private val properties: Map<String, String>) {

    fun getParameter(key: String): String {
        return properties[key] ?: throw IllegalArgumentException("Parameter '$key' not specified in properties")
    }

    companion object {
        val NONE: ParameterStore = ParameterStore(mapOf())

        fun loadProperties(filePath: String? = null, defaultPath: String = System.getProperty("user.home") + File.separator + "unifile.properties", logger: Logger? = null): ParameterStore {
            val propertiesFile = File(filePath ?: defaultPath)
            val properties = Properties()

            val exists = propertiesFile.exists()
            if (exists) {
                propertiesFile.inputStream().use {
                    properties.load(it)
                }
            } else {
                logger?.warn("Properties file not found: ${propertiesFile.path}")
            }

            val propertiesMap = properties.mapKeys { it.key.toString() }.mapValues { it.value.toString() }
            val parameterStore = ParameterStore(propertiesMap)
            return parameterStore
        }
    }
}
