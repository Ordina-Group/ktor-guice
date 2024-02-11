package com.ordina

import com.google.inject.Inject
import com.google.inject.Provider
import com.typesafe.config.Config
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.util.reflect.*
import kotlin.reflect.KClass

class ApplicationEngineProvider @Inject constructor(
    private val config: ApplicationConfiguration
) : Provider<ApplicationEngine> {
    override fun get(): ApplicationEngine {
        val engineFactory = getEngineFactory(config.engine)

        return embeddedServer(engineFactory, port = config.port, host = config.host) {}
    }

    private fun getEngineFactory(engineName: String): ApplicationEngineFactory<*, *> {
        val clazz = Class.forName(engineName).kotlin.objectInstance

        if (clazz is ApplicationEngineFactory<*, *>) {
            return clazz
        } else {
            throw Exception("Unable to resolve engine with qualified name $engineName")
        }
    }

}

data class ApplicationConfiguration(
    val engine: String,
    val host: String,
    val port: Int
) {
    @Inject
    constructor(config: Config): this(
        engine = config.getString("ktor.engine"),
        host = config.getString("ktor.host"),
        port = config.getInt("ktor.port")
    )
}
