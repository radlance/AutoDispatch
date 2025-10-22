package com.github.radlance.autodispatch.plugins

import com.github.radlance.autodispatch.route.requestRoute
import com.github.radlance.autodispatch.route.auth
import com.github.radlance.autodispatch.route.profile
import com.github.radlance.autodispatch.service.AuthService
import com.github.radlance.autodispatch.service.ProfileService
import com.github.radlance.autodispatch.service.RequestService
import io.ktor.server.application.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Application.configureRouting() {
    val authService by inject<AuthService>()
    val profileService by inject<ProfileService>()
    val requestService by inject<RequestService>()

    routing {
        route("/api") {
            auth(authService)
            profile(profileService)
            requestRoute(requestService)
        }
    }
}
