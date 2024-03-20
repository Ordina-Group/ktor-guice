package com.ordina.kuice.ktor.routes

import com.google.inject.Injector
import com.ordina.kuice.Registry
import io.ktor.http.HttpMethod
import io.ktor.server.application.ApplicationCall
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.patch
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.Route as KRoute

typealias RequestHandler = suspend io.ktor.util.pipeline.PipelineContext<Unit, ApplicationCall>.(Unit) -> Unit

data class Route<Controller, RequestHandler>(
    val getRequestHandler: Controller.() -> RequestHandler,
    val getRoute: (RequestHandler) -> KRoute.() -> Unit,
    val clazz: Class<Controller>
) {
    fun getRoute(injector: Injector): KRoute.() -> Unit {
        val controller = injector.getInstance(clazz)
        val requestHandler = getRequestHandler.invoke(controller)

        return getRoute(requestHandler)
    }
}

class RouteScope(val registry : Registry<Route<*, *>>) {

    inline fun <reified T> get(path: String, noinline getRequestHandler: T.() -> RequestHandler) =
        registry.register(
            Route(getRequestHandler, { handler -> { this.get(path, handler) } }, T::class.java)
        )

    inline fun <reified T> post(path: String, noinline getRequestHandler: T.() -> RequestHandler) =
        registry.register(
            Route(getRequestHandler, { handler -> { this.post(path, handler) } }, T::class.java)
        )

    inline fun <reified T> put(path: String, noinline getRequestHandler: T.() -> RequestHandler) =
        registry.register(
            Route(getRequestHandler, { handler -> { this.put(path, handler) } }, T::class.java)
        )

    inline fun <reified T> delete(path: String, noinline getRequestHandler: T.() -> RequestHandler) =
        registry.register(
            Route(getRequestHandler, { handler -> { this.delete(path, handler) } }, T::class.java)
        )

    inline fun <reified T> patch(path: String, noinline getRequestHandler: T.() -> RequestHandler) =
        registry.register(
            Route(getRequestHandler, { handler -> { this.patch(path, handler) } }, T::class.java)
        )
}

object RouteRegistry : Registry<Route<*, *>>()
