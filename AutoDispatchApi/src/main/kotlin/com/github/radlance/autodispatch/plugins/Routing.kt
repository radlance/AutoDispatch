package com.github.radlance.autodispatch.plugins

import com.github.radlance.autodispatch.route.auth
import com.github.radlance.autodispatch.route.profile
import com.github.radlance.autodispatch.service.AuthService
import com.github.radlance.autodispatch.service.ProfileService
import io.ktor.server.application.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Application.configureRouting() {
    val authService by inject<AuthService>()
    val profileService by inject<ProfileService>()

    routing {
        route("/api") {
            auth(authService)
            profile(profileService)
        }
    }
}
