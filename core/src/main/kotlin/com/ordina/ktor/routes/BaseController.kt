package com.ordina.ktor.routes

interface BaseController {
    fun request(f: RequestHandler): RequestHandler = f
}
