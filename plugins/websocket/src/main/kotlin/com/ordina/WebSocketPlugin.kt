package com.ordina

import com.google.inject.Inject
import com.google.inject.ProvidedBy
import com.ordina.config.ConfigLoader
import com.ordina.config.ConfigProvider
import com.ordina.config.getClass
import com.ordina.config.getOptionalClass
import com.ordina.config.getOptionalLong
import com.ordina.ktor.plugins.BaseApplicationPlugin
import com.typesafe.config.Config
import io.ktor.serialization.WebsocketContentConverter
import io.ktor.server.websocket.WebSockets
import io.ktor.server.websocket.WebSockets.WebSocketOptions
import io.ktor.server.websocket.pingPeriod
import io.ktor.server.websocket.timeout
import java.time.Duration

class WebSocketPlugin(config: WebSocketConfiguration) : BaseApplicationPlugin<WebSocketOptions, WebSockets>(WebSockets, {
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

