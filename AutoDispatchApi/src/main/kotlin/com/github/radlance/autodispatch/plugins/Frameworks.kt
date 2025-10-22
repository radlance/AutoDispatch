package com.github.radlance.autodispatch.plugins

import com.github.radlance.autodispatch.di.authModule
import com.github.radlance.autodispatch.di.profileModule
import com.github.radlance.autodispatch.di.requestModule
import io.ktor.server.application.*
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

fun Application.configureFrameworks() {
    install(Koin) {
        slf4jLogger()
        modules(authModule, profileModule, requestModule)
    }
}
