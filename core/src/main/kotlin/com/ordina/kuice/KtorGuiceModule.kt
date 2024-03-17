package com.ordina.kuice

import com.google.inject.AbstractModule
import com.google.inject.Provides
import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import io.ktor.server.application.Application
import io.ktor.server.engine.ApplicationEngine

object KtorGuiceModule : AbstractModule() {

    override fun configure() {
        bind(ApplicationEngine::class.java).toProvider(ApplicationEngineProvider::class.java).asEagerSingleton()
    }

    @Provides
    fun provideConfig(): Config = ConfigFactory.load()

    @Provides
    fun provideApplication(engine: ApplicationEngine): Application = engine.application
}
