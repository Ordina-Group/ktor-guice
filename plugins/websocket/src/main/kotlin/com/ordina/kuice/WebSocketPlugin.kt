package com.ordina.kuice

import com.google.inject.Inject
import com.google.inject.ProvidedBy
import com.ordina.kuice.config.ConfigLoader
import com.ordina.kuice.config.ConfigProvider
import com.ordina.kuice.config.getOptionalClass
import com.ordina.kuice.config.getOptionalLong
import com.ordina.kuice.ktor.plugins.BaseApplicationPlugin
import com.ordina.kuice.ktor.routes.RouteScope
import com.typesafe.config.Config
import io.ktor.serialization.WebsocketContentConverter
import io.ktor.server.websocket.DefaultWebSocketServerSession
import io.ktor.server.websocket.WebSockets
import io.ktor.server.websocket.WebSockets.WebSocketOptions
import io.ktor.server.websocket.pingPeriod
import io.ktor.server.websocket.timeout
import java.time.Duration

class WebSocketPlugin @Inject constructor(config: WebSocketConfiguration) : BaseApplicationPlugin<WebSocketOptions, WebSockets>(WebSockets, {
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

fun RouteScope.webSocket(
    path: String,
    protocol: String? = null,
    handler: suspend DefaultWebSocketServerSession.() -> Unit) {
//    registry.register(Route(HttpMethod.Get, path, handler))
}
interface WebSocketController {
    fun request(f: suspend DefaultWebSocketServerSession.() -> Unit): suspend DefaultWebSocketServerSession.() -> Unit = f
}


