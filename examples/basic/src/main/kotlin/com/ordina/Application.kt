package com.ordina

import com.google.inject.Guice
import com.google.inject.Injector
import kotlin.reflect.KFunction

fun main() {
    guiceApplication {
        routes {
            get<SimpleController>("/foo") { getX }
            get<SimpleController>("/bar") { getY }
        }
    }
}




