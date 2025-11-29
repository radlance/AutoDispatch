package com.github.radlance.autodispatch.plugins

import com.github.radlance.autodispatch.repository.DeliveryRepository
import com.github.radlance.autodispatch.repository.ProfileRepository
import com.github.radlance.autodispatch.repository.RequestRepository
import com.github.radlance.autodispatch.route.auth
import com.github.radlance.autodispatch.route.deliveries
import com.github.radlance.autodispatch.route.profile
import com.github.radlance.autodispatch.route.requests
import com.github.radlance.autodispatch.service.AuthService
import io.ktor.server.application.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Application.configureRouting() {
    val authService by inject<AuthService>()
    val profileRepository by inject<ProfileRepository>()
    val requestRepository by inject<RequestRepository>()
    val deliveryRepository by inject<DeliveryRepository>()

    routing {
        route("/api") {
            auth(authService)
            profile(profileRepository)
            requests(requestRepository)
            deliveries(deliveryRepository)
        }
    }
}
