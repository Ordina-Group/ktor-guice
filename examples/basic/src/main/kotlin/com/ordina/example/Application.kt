package com.ordina.example

import com.ordina.guiceApplication

fun main() {
    guiceApplication {
        routes {
            post<SimpleController>("/foo") { getX }
            get<SimpleController>("/bar") { getY }
        }
    }
}




