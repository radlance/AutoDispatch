package com.github.radlance.autodispatch

import com.github.radlance.autodispatch.plugins.configureBroker
import com.github.radlance.autodispatch.plugins.configureDatabases
import com.github.radlance.autodispatch.plugins.configureDi
import com.github.radlance.autodispatch.plugins.configureHTTP
import com.github.radlance.autodispatch.plugins.configureMonitoring
import com.github.radlance.autodispatch.plugins.configureRouting
import com.github.radlance.autodispatch.plugins.configureSecurity
import com.github.radlance.autodispatch.plugins.configureSerialization
import com.github.radlance.autodispatch.plugins.configureValidation
import io.ktor.server.application.*
import io.ktor.server.netty.EngineMain

fun main(args: Array<String>) {
    EngineMain.main(args)
}

fun Application.module() {
    configureMonitoring()
    configureDatabases(config = environment.config)
    configureDi()
    configureBroker()
    configureValidation()
    configureSerialization()
    configureHTTP()
    configureSecurity()
    configureRouting()
}
