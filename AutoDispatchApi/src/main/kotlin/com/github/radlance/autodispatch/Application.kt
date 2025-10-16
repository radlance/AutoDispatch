package com.github.radlance.com.github.radlance.autodispatch

import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureDatabases(config = environment.config)
    configureSockets()
    configureFrameworks()
    configureSerialization()
    configureHTTP()
    configureRouting()
}
