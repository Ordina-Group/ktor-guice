package com.ordina

import com.ordina.ktor.plugins.Plugin
import io.ktor.server.application.install
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNamingStrategy

@OptIn(ExperimentalSerializationApi::class)
class JsonSerializationPlugin(config: JsonSerializationConfiguration) : Plugin({
    install(ContentNegotiation) {
        Json {
            encodeDefaults = config.encodeDefaults
            ignoreUnknownKeys = config.ignoreUnknownKeys
            isLenient = config.isLenient
            allowStructuredMapKeys = config.allowStructuredMapKeys
            prettyPrint = config.prettyPrint
            explicitNulls = config.explicitNulls
            prettyPrintIndent = config.prettyPrintIndent
            coerceInputValues = config.coerceInputValues
            useArrayPolymorphism = config.useArrayPolymorphism
            classDiscriminator = config.classDiscriminator
            allowSpecialFloatingPointValues = config.allowSpecialFloatingPointValues
        }
    }
})

data class JsonSerializationConfiguration @OptIn(ExperimentalSerializationApi::class) constructor(
    val encodeDefaults: Boolean = false,
    val ignoreUnknownKeys: Boolean = false,
    val isLenient: Boolean = false,
    val allowStructuredMapKeys: Boolean = false,
    val prettyPrint: Boolean = false,
    val explicitNulls: Boolean = true,
    val prettyPrintIndent: String = "    ",
    val coerceInputValues: Boolean = false,
    val useArrayPolymorphism: Boolean = false,
    val classDiscriminator: String = "type",
    val allowSpecialFloatingPointValues: Boolean = false,
    val useAlternativeNames: Boolean = true,
    val namingStrategy: JsonNamingStrategy? = null,
)
