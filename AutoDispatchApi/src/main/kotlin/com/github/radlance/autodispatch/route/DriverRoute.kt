package com.github.radlance.autodispatch.route

import com.github.radlance.autodispatch.repository.DriverRepository
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.authenticate
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route

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
        }
    }
}