package com.ordina.kuice

import com.google.inject.Guice
import com.google.inject.Injector
import com.google.inject.Module
import com.ordina.kuice.guice.getInstance
import com.ordina.kuice.ktor.plugins.BaseApplicationPlugin
import com.ordina.kuice.ktor.plugins.BaseApplicationPluginWithRoutes
import com.ordina.kuice.ktor.plugins.BasePlugin
import com.ordina.kuice.ktor.plugins.BaseRouteScopedPlugin
import com.ordina.kuice.ktor.routes.RouteRegistry
import com.ordina.kuice.ktor.routes.RouteScope
import com.typesafe.config.Config
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationCall
import io.ktor.server.engine.ApplicationEngine
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import io.ktor.util.pipeline.Pipeline
import org.slf4j.LoggerFactory

internal val applicationLogger = LoggerFactory.getLogger("com.ordina.kuice.Application")

class GuiceApplicationScope {
    fun routes(f: RouteScope.() -> Unit) = f.invoke(RouteScope(RouteRegistry))
}

fun guiceApplication(
    modules: List<Module>,
    f: GuiceApplicationScope.() -> Unit
) {
    val injector = Guice.createInjector(listOf(KtorGuiceModule) + modules)
    val engine = injector.getInstance<ApplicationEngine>()
    val config = injector.getInstance<Config>()

    val plugins = getPlugins(injector, config)

    engine.application.apply {
        plugins.forEach { plugin ->
            applicationLogger.debug("Installing plugin: ${plugin.javaClass.simpleName}")
            plugin.install(this)
        }
    }

    f(GuiceApplicationScope())

    engine.application.apply {
        routing {
            RouteRegistry.values().forEach { r ->
                r.getRoute(injector).invoke(this)
            }

            plugins
                .filterIsInstance<BaseApplicationPluginWithRoutes<*, *>>()
                .forEach { plugin ->
                    plugin.setupRoutes(this)
                }
        }
    }

    engine.start(wait = true)
}

fun guiceApplication(f: GuiceApplicationScope.() -> Unit) = guiceApplication(emptyList(), f)

private fun getPlugins(injector: Injector, config: Config): List<BasePlugin<Pipeline<*, ApplicationCall>, *, *>> =
    config
        .getStringList("ktor.plugins")
        .map { Class.forName(it) }
        .map { injector.getInstance(it) }
        .filterIsInstance<BasePlugin<Pipeline<*, ApplicationCall>, *, *>>()
