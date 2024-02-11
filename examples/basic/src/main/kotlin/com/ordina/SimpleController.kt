package com.ordina

import com.ordina.ktor.routes.BaseController
import com.typesafe.config.Config
import io.ktor.server.application.call
import io.ktor.server.response.respond
import jakarta.inject.Inject
import jakarta.inject.Singleton

@Singleton
class SimpleController @Inject constructor(private val config: Config) : BaseController {
    val getX = request {
        call.respond("foo")
    }

    val getY = request {
        call.respond("bar")
    }
}