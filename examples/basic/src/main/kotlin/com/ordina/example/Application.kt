package com.ordina.example

import com.ordina.kuice.application

fun main() {
    application {
        routes {
            get<SimpleController>("/greeting") { getGreeting }
            post<SimpleController>("/greeting") { getGreetingWithName }
        }
    }
}




