package com.ordina.kuice.ktor.routes

interface BaseController {
    fun request(f: RequestHandler): RequestHandler = f
}
