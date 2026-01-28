package com.github.radlance.autodispatch.plugins

import com.github.radlance.autodispatch.repository.*
import com.github.radlance.autodispatch.route.*
import com.github.radlance.autodispatch.service.AuthService
import com.github.radlance.autodispatch.service.DeliveryService
import com.github.radlance.autodispatch.service.DocumentsService
import com.github.radlance.autodispatch.service.RequestService
import io.ktor.server.application.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Application.configureRouting() {
    val authService by inject<AuthService>()
    val profileRepository by inject<ProfileRepository>()
    val requestService by inject<RequestService>()
    val deliveryService by inject<DeliveryService>()
    val documentsService by inject<DocumentsService>()
    val driverRepository by inject<DriverRepository>()
    val vehicleRepository by inject<VehicleRepository>()
    val statisticsRepository by inject<StatisticsRepository>()

    routing {
        route("/api") {
            auth(authService)
            profile(profileRepository)
            requests(requestService)
            deliveries(deliveryService)
            documents(documentsService)
            driver(driverRepository)
            vehicle(vehicleRepository)
            statistics(statisticsRepository)
        }
    }
}
