package com.github.radlance.autodispatch.route

import com.github.radlance.autodispatch.service.RequestService
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.requestRoute(requestService: RequestService) {
    authenticate {
        route("/requests") {
            get {
                val queryParams = call.request.queryParameters

                val page = queryParams["page"]?.toIntOrNull() ?: 1
                val pageSize = queryParams["pageSize"]?.toIntOrNull() ?: 20

                val searchQuery = queryParams["search"]

                fun parseIds(paramName: String): List<Int> {
                    return queryParams[paramName]
                        ?.split(',')
                        ?.mapNotNull { it.trim().toIntOrNull() }
                        ?: emptyList()
                }

                val originCityIds = parseIds("originCityIds")
                val destinationCityIds = parseIds("destinationCityIds")
                val cargoTypeIds = parseIds("cargoTypeIds")
                val statusIds = parseIds("statusIds")
                val driverIds = parseIds("driverIds")
                val vehicleIds = parseIds("vehicleIds")

                val paginatedRequests = requestService.requests(
                    page = page,
                    pageSize = pageSize,
                    searchQuery = searchQuery,
                    originCityIds = originCityIds,
                    destinationCityIds = destinationCityIds,
                    cargoTypeIds = cargoTypeIds,
                    statusIds = statusIds,
                    driverIds = driverIds,
                    vehicleIds = vehicleIds
                )

                call.respond(HttpStatusCode.OK, paginatedRequests)
            }

            get("/filters") {
                val requests = requestService.filters()
                call.respond(HttpStatusCode.OK, requests)
            }
        }
    }
}