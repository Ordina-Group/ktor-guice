package com.ordina

import com.google.inject.Guice
import com.ordina.guice.getInstance
import com.ordina.ktor.plugins.PluginRegistry
import com.ordina.ktor.routes.RouteRegistry
import com.ordina.ktor.routes.RouteScope
import io.ktor.server.application.plugin
import io.ktor.server.engine.ApplicationEngine
import io.ktor.server.routing.route
import io.ktor.server.routing.routing

class GuiceApplicationScope {
    fun routes(f: RouteScope.() -> Unit) = f.invoke(RouteScope())
}

fun guiceApplication(f: GuiceApplicationScope.() -> Unit) {
    val injector = Guice.createInjector(KtorGuiceModule)
    val engine = injector.getInstance<ApplicationEngine>()

    f(GuiceApplicationScope())

    engine.application.apply {
        routing {
            RouteRegistry.values().forEach { r ->
                val body = r.getRoute(injector)

                route(r.path, r.method) { handle(body) }
            }
        }

        PluginRegistry.values().forEach { r ->
            plugin(r)
        }
    }

    engine.start(wait = true)
}


