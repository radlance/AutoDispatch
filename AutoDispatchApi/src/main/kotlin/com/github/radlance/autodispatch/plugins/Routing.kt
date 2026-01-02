package com.github.radlance.autodispatch.plugins

import com.github.radlance.autodispatch.repository.DeliveryRepository
import com.github.radlance.autodispatch.repository.DocumentsRepository
import com.github.radlance.autodispatch.repository.DriverRepository
import com.github.radlance.autodispatch.repository.ProfileRepository
import com.github.radlance.autodispatch.repository.RequestRepository
import com.github.radlance.autodispatch.repository.StatisticsRepository
import com.github.radlance.autodispatch.repository.VehicleRepository
import com.github.radlance.autodispatch.route.auth
import com.github.radlance.autodispatch.route.deliveries
import com.github.radlance.autodispatch.route.document
import com.github.radlance.autodispatch.route.driver
import com.github.radlance.autodispatch.route.profile
import com.github.radlance.autodispatch.route.requests
import com.github.radlance.autodispatch.route.statistics
import com.github.radlance.autodispatch.route.vehicle
import com.github.radlance.autodispatch.service.AuthService
import io.ktor.server.application.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Application.configureRouting() {
    val authService by inject<AuthService>()
    val profileRepository by inject<ProfileRepository>()
    val requestRepository by inject<RequestRepository>()
    val deliveryRepository by inject<DeliveryRepository>()
    val documentRepository by inject<DocumentsRepository>()
    val driverRepository by inject<DriverRepository>()
    val vehicleRepository by inject<VehicleRepository>()
    val statisticsRepository by inject<StatisticsRepository>()

    routing {
        route("/api") {
            auth(authService)
            profile(profileRepository)
            requests(requestRepository)
            deliveries(deliveryRepository)
            document(documentRepository)
            driver(driverRepository)
            vehicle(vehicleRepository)
            statistics(statisticsRepository)
        }
    }
}
