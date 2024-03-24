package com.ordina.kuice

import com.google.inject.Guice
import com.google.inject.Injector
import com.google.inject.Module
import com.ordina.kuice.config.getOptional
import com.ordina.kuice.guice.getInstance
import com.ordina.kuice.ktor.plugins.BaseApplicationPluginWithRoutes
import com.ordina.kuice.ktor.plugins.BasePlugin
import com.ordina.kuice.ktor.routes.Route
import com.ordina.kuice.ktor.routes.RouteScope
import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import io.ktor.server.application.ApplicationCall
import io.ktor.server.engine.ApplicationEngine
import io.ktor.server.routing.routing
import io.ktor.util.pipeline.Pipeline
import org.slf4j.LoggerFactory

internal val applicationLogger = LoggerFactory.getLogger("com.ordina.kuice.Application")

class ApplicationScope(private val routeRegistry: Registry<Route>) {
    fun routes(f: RouteScope.() -> Unit) = f.invoke(RouteScope(routeRegistry))
}

fun application(
    f: ApplicationScope.() -> Unit
) {
    val config = ConfigFactory.load()

    val pluginModules = getModules(config, "ktor.pluginModules")

    applicationLogger.warn("Loading ${pluginModules.size} plugin modules")

    val modules = getModules(config, "ktor.modules")

    val injector = Guice.createInjector(listOf(KtorGuiceModule(config)) + pluginModules + modules)
    val engine = injector.getInstance<ApplicationEngine>()

    val plugins = getPlugins(injector, config)
    val routeRegistry = object : Registry<Route>() {}

    engine.application.apply {
        plugins.forEach { plugin ->
            applicationLogger.debug("Installing plugin: ${plugin.javaClass.simpleName}")
            plugin.install(this)
        }
    }

    f(ApplicationScope(routeRegistry))

    engine.application.apply {
        routing {
            applicationLogger.warn("Registering ${routeRegistry.values().size} routes")
            routeRegistry.values().forEach { r ->
                applicationLogger.warn("Registering route $r")

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

private fun getPlugins(injector: Injector, config: Config): List<BasePlugin<Pipeline<*, ApplicationCall>, *, *>> =
    (config.getOptional("ktor.plugins", config::getStringList) ?: emptyList())
        .map { Class.forName(it) }
        .map { injector.getInstance(it) }
        .filterIsInstance<BasePlugin<Pipeline<*, ApplicationCall>, *, *>>()

private fun getModules(config: Config, path: String): List<Module> =
    (config.getOptional(path, config::getStringList) ?: emptyList())
        .map { Class.forName(it).kotlin.objectInstance }
        .filterIsInstance<Module>()
