package com.github.radlance.autodispatch.plugins

import com.github.radlance.autodispatch.di.authModule
import io.ktor.server.application.*
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

fun Application.configureFrameworks() {
    install(Koin) {
        slf4jLogger()
        modules(authModule)
    }
}
