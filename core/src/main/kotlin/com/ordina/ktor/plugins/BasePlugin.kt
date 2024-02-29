package com.ordina.ktor.plugins

import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.ApplicationCallPipeline
import io.ktor.server.application.Plugin
import io.ktor.server.application.install
import io.ktor.util.pipeline.Pipeline


abstract class BaseApplicationPlugin<TConfiguration : Any, TPlugin : Any>(
    private val plugin: Plugin<Application, TConfiguration, TPlugin>,
    private val configure: TConfiguration.() -> Unit
) {
    fun install(application: Application) {
        application.install(plugin, configure)
    }
}

abstract class BaseRouteScopedPlugin<TConfiguration : Any, TPlugin : Any>(
    private val plugin: Plugin<ApplicationCallPipeline, TConfiguration, TPlugin>,
    private val configure: TConfiguration.() -> Unit
) {
    fun install(pipeline: ApplicationCallPipeline) {
        pipeline.install(plugin, configure)
    }
}
