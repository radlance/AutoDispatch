package com.github.radlance.autodispatch.route

import com.github.radlance.autodispatch.repository.DriverRepository
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.driver(repository: DriverRepository) {
    authenticate {
        route("/drivers") {
            get {
                val queryParams = call.request.queryParameters
                val page = queryParams["page"]?.toIntOrNull() ?: 1
                val pageSize = queryParams["pageSize"]?.toIntOrNull() ?: 20
                val searchQuery = queryParams["search"]

                val paginatedRequests = repository.drivers(
                    page = page,
                    pageSize = pageSize,
                    searchQuery = searchQuery
                )

                call.respond(HttpStatusCode.OK, paginatedRequests)
            }

            get("/assignments") {
                val queryParams = call.request.queryParameters
                val page = queryParams["page"]?.toIntOrNull() ?: 1
                val pageSize = queryParams["pageSize"]?.toIntOrNull() ?: 20
                val searchQuery = queryParams["search"]
                val stats = repository.driverStats(
                    page = page,
                    pageSize = pageSize,
                    searchQuery = searchQuery
                )
                call.respond(HttpStatusCode.OK, stats)
            }
        }
    }
}