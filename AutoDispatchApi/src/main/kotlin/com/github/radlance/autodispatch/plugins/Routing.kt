package com.github.radlance.autodispatch.plugins

import com.github.radlance.autodispatch.repository.DriverRepository
import com.github.radlance.autodispatch.repository.ProfileRepository
import com.github.radlance.autodispatch.repository.StatisticsRepository
import com.github.radlance.autodispatch.repository.VehicleRepository
import com.github.radlance.autodispatch.route.auth
import com.github.radlance.autodispatch.route.deliveries
import com.github.radlance.autodispatch.route.documents
import com.github.radlance.autodispatch.route.driver
import com.github.radlance.autodispatch.route.profile
import com.github.radlance.autodispatch.route.requests
import com.github.radlance.autodispatch.route.statistics
import com.github.radlance.autodispatch.route.vehicle
import com.github.radlance.autodispatch.service.AuthService
import com.github.radlance.autodispatch.service.DeliveryService
import com.github.radlance.autodispatch.service.DocumentsService
import com.github.radlance.autodispatch.service.ReportService
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
    val reportService by inject<ReportService>()

    routing {
        route("/api") {
            auth(authService)
            profile(profileRepository)
            requests(requestService)
            deliveries(deliveryService)
            documents(documentsService)
            driver(driverRepository)
            vehicle(vehicleRepository)
            statistics(statisticsRepository, reportService)
        }
    }
}
