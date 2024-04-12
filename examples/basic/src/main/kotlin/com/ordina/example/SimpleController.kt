package com.ordina.example

import com.ordina.kuice.ktor.routes.ApplicationController
import com.typesafe.config.Config
import io.ktor.server.application.call
import io.ktor.server.request.*
import io.ktor.server.response.respond
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.serialization.Serializable

@Singleton
class SimpleController @Inject constructor(private val config: Config) : ApplicationController {
    val getGreeting = request {
        call.respond("Hello world!")

    }

    val getGreetingWithName = request {
        val input = call.receive<RequestInput>()
        call.respond("Hello ${input.name}")
    }
}

@Serializable
data class RequestInput(val name: String)