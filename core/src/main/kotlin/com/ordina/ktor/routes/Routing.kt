package com.ordina.ktor.routes

import com.google.inject.Injector
import com.ordina.Registry
import io.ktor.http.HttpMethod
import io.ktor.server.application.ApplicationCall

typealias RequestHandler = suspend io.ktor.util.pipeline.PipelineContext<Unit, ApplicationCall>.(Unit) -> Unit

data class Route<T>(val method: HttpMethod, val path: String, val f: T.() -> RequestHandler, val clazz: Class<T>) {
    fun getRoute(injector: Injector): RequestHandler {
        val t = injector.getInstance(clazz)

        return f.invoke(t)
    }
}

class RouteScope {
    inline fun <reified T> get(path: String, noinline f: T.() -> RequestHandler) =
        RouteRegistry.register(Route(HttpMethod.Get, path, f, T::class.java))

    inline fun <reified T> post(path: String, noinline f: T.() -> RequestHandler) =
        RouteRegistry.register(Route(HttpMethod.Post, path, f, T::class.java))

    inline fun <reified T> put(path: String, noinline f: T.() -> RequestHandler) =
        RouteRegistry.register(Route(HttpMethod.Put, path, f, T::class.java))

    inline fun <reified T> delete(path: String, noinline f: T.() -> RequestHandler) =
        RouteRegistry.register(Route(HttpMethod.Delete, path, f, T::class.java))

    inline fun <reified T> patch(path: String, noinline f: T.() -> RequestHandler) =
        RouteRegistry.register(Route(HttpMethod.Patch, path, f, T::class.java))
}

object RouteRegistry : Registry<Route<*>>()