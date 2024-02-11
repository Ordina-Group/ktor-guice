package com.ordina.ktor.plugins

import io.ktor.server.application.Application

abstract class Plugin(private val body: Application.() -> Unit) {
    fun install(application: Application): Unit = body(application)
}
