package com.ordina

import com.ordina.ktor.routes.BaseController

interface ControllerModule {
    val controllers: List<BaseController>
}