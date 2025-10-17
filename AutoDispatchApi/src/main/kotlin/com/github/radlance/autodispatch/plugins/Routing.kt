package com.github.radlance.autodispatch.plugins

import com.github.radlance.autodispatch.route.auth
import com.github.radlance.autodispatch.service.AuthService
import io.ktor.server.application.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Application.configureRouting() {
    val authService by inject<AuthService>()

    routing {
        route("/api") {
            auth(authService)
        }
    }
}
