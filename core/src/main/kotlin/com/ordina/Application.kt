package com.ordina

import com.google.inject.Guice
import com.google.inject.Module
import com.ordina.guice.getInstance
import com.ordina.ktor.plugins.BaseRouteScopedPlugin
import com.ordina.ktor.routes.RouteRegistry
import com.ordina.ktor.routes.RouteScope
import com.typesafe.config.Config
import io.ktor.server.engine.ApplicationEngine
import io.ktor.server.routing.route
import io.ktor.server.routing.routing

class GuiceApplicationScope {
    fun routes(f: RouteScope.() -> Unit) = f.invoke(RouteScope())
}

fun guiceApplication(modules: List<Module>, f: GuiceApplicationScope.() -> Unit) {
    val injector = Guice.createInjector(listOf(KtorGuiceModule) + modules)
    val engine = injector.getInstance<ApplicationEngine>()
    val config = injector.getInstance<Config>()

    val plugins = config.getStringList("ktor.plugins")

    engine.application.apply {
        plugins.forEach { pluginName ->
            val plugin = injector.getInstance(Class.forName(pluginName))

            if (plugin is BaseRouteScopedPlugin<*, *>) {
                println("Installing plugin: ${plugin.javaClass.simpleName}")
                plugin.install(this)
            }
        }
    }

    f(GuiceApplicationScope())

    engine.application.apply {
        routing {
            RouteRegistry.values().forEach { r ->
                val body = r.getRoute(injector)

                route(r.path, r.method) { handle(body) }
            }
        }


    }

    engine.start(wait = true)
}

fun guiceApplication(f: GuiceApplicationScope.() -> Unit) = guiceApplication(emptyList(), f)
