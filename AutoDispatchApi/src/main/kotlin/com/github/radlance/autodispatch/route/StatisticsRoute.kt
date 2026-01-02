package com.github.radlance.autodispatch.route

import com.github.radlance.autodispatch.repository.StatisticsRepository
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.statistics(repository: StatisticsRepository) {
    authenticate {
        route("/statistics") {
            get {
                val statistics = repository.getStatistics()
                call.respond(HttpStatusCode.OK, statistics)
            }
        }
    }
}