package com.ordina.ktor.plugins

import com.ordina.Registry
import io.ktor.server.application.Plugin

object PluginRegistry : Registry<Plugin<*, *, *>>()