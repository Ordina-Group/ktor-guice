package com.ordina.kuice.ktor.routes

import com.google.inject.Injector
import com.ordina.kuice.Registry
import io.ktor.server.application.ApplicationCall
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.patch
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route
import org.slf4j.LoggerFactory
import io.ktor.server.routing.Route as KRoute

typealias RequestHandler = suspend io.ktor.util.pipeline.PipelineContext<Unit, ApplicationCall>.(Unit) -> Unit

interface Route {
    fun getRoute(injector: Injector): KRoute.() -> Unit
}

class BaseRoute<Controller, RequestHandler>(
    private val getRequestHandler: Controller.() -> RequestHandler,
    private val getRouteFromHandler: (RequestHandler) -> KRoute.() -> Unit,
    private val clazz: Class<Controller>,
) : Route {
    override fun getRoute(injector: Injector): KRoute.() -> Unit {
        val controller = injector.getInstance(clazz)
        val requestHandler = getRequestHandler.invoke(controller)

        return getRouteFromHandler(requestHandler)
    }
}

class ParentRoute(
    private val parentRoute: (KRoute, KRoute.() -> Unit) -> Unit,
    private val childrenRoutes: List<Route>,
) : Route {

    override fun getRoute(injector: Injector): KRoute.() -> Unit = {
        parentRoute.invoke(this) {
            childrenRoutes.forEach { it.getRoute(injector).invoke(this) }
        }
    }
}

private val logger = LoggerFactory.getLogger("com.ordina.kuice.Routing")

class RouteScope(val registry: Registry<Route>) {

    inline fun <reified T> get(path: String, noinline getRequestHandler: T.() -> RequestHandler) =
        registry.register(
            BaseRoute(getRequestHandler, { handler -> { this.get(path, handler) } }, T::class.java),
        )

    inline fun <reified T> post(path: String, noinline getRequestHandler: T.() -> RequestHandler) =
        registry.register(
            BaseRoute(getRequestHandler, { handler -> { this.post(path, handler) } }, T::class.java),
        )

    inline fun <reified T> put(path: String, noinline getRequestHandler: T.() -> RequestHandler) =
        registry.register(
            BaseRoute(getRequestHandler, { handler -> { this.put(path, handler) } }, T::class.java),
        )

    inline fun <reified T> delete(path: String, noinline getRequestHandler: T.() -> RequestHandler) =
        registry.register(
            BaseRoute(getRequestHandler, { handler -> { this.delete(path, handler) } }, T::class.java),
        )

    inline fun <reified T> patch(path: String, noinline getRequestHandler: T.() -> RequestHandler) =
        registry.register(
            BaseRoute(getRequestHandler, { handler -> { this.patch(path, handler) } }, T::class.java),
        )

    fun route(path: String, f: RouteScope.() -> Unit) {
        val childRegistry = object : Registry<Route>() { }
        val childScope = RouteScope(childRegistry)

        f(childScope)

        fun getParentRoute(route: KRoute, build: KRoute.() -> Unit): KRoute =
            route.route(path, build)

        registry.register(
            ParentRoute(::getParentRoute, childRegistry.values()),
        )
    }
}
