package com.ordina.kuice

import com.google.inject.Inject
import com.google.inject.Injector
import com.google.inject.ProvidedBy
import com.ordina.kuice.config.ConfigLoader
import com.ordina.kuice.config.ConfigProvider
import com.ordina.kuice.config.getOptionalClass
import com.ordina.kuice.config.getOptionalLong
import com.ordina.kuice.ktor.plugins.BaseApplicationPlugin
import com.ordina.kuice.ktor.routes.BaseController
import com.ordina.kuice.ktor.routes.Route
import com.ordina.kuice.ktor.routes.RouteScope
import com.typesafe.config.Config
import io.ktor.serialization.WebsocketContentConverter
import io.ktor.server.websocket.DefaultWebSocketServerSession
import io.ktor.server.websocket.WebSockets
import io.ktor.server.websocket.WebSockets.WebSocketOptions
import io.ktor.server.websocket.pingPeriod
import io.ktor.server.websocket.timeout
import io.ktor.server.websocket.webSocket
import java.time.Duration

typealias WebSocketRequestHandler = suspend DefaultWebSocketServerSession.() -> Unit

class WebSocketPlugin @Inject constructor(private val injector: Injector, config: WebSocketConfiguration) : BaseApplicationPlugin<WebSocketOptions, WebSockets>(WebSockets, {
    pingPeriod = config.pingPeriodMillis
    timeout = config.timeout
    maxFrameSize = config.maxFrameSize
    contentConverter = config.contentConverter
})

@ProvidedBy(WebSocketConfigProvider::class)
data class WebSocketConfiguration(
    val pingPeriodMillis: Duration,
    val timeout: Duration,
    val maxFrameSize: Long,
    val masking: Boolean,
    val contentConverter: WebsocketContentConverter?
)

class WebSocketConfigProvider @Inject constructor(config: Config) :
    ConfigProvider<WebSocketConfiguration>(WebSocketConfigLoader, config)

object WebSocketConfigLoader :
    ConfigLoader<WebSocketConfiguration>("ktor.websocket", {
        WebSocketConfiguration(
            pingPeriodMillis = getDuration("pingPeriodMillis"),
            timeout = getDuration("timeout"),
            maxFrameSize = getOptionalLong("maxFrameSize") ?: Long.MAX_VALUE,
            masking = getBoolean("masking"),
            contentConverter = getOptionalClass("contentConvert")
        )
    })

inline fun <reified T> RouteScope.webSocket(
    path: String,
    protocol: String? = null,
    noinline getRequestHandler: T.() -> WebSocketRequestHandler) {
    registry.register(
        Route(getRequestHandler, { handler -> { this.webSocket(path, protocol, handler) } }, T::class.java)
    )
}

interface BaseWebSocketController : BaseController<WebSocketRequestHandler>
