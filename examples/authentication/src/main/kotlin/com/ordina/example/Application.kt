package com.ordina.example

import com.ordina.kuice.authenticate
import com.ordina.kuice.guiceApplication

fun main() {
    guiceApplication {
        routes {
            authenticate {
                get<SimpleController>("/foo") { getX }
            }
        }
    }
}




