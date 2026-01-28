package com.github.radlance.autodispatch.plugins

import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureDatabases(config = environment.config)
    configureDi()
    configureBroker()
    configureValidation()
    configureSerialization()
    configureHTTP()
    configureSecurity()
    configureRouting()
}
