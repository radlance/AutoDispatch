package com.github.radlance.autodispatch.route

import com.github.radlance.autodispatch.domain.request.AssignRequest
import com.github.radlance.autodispatch.domain.request.CreateRequest
import com.github.radlance.autodispatch.service.RequestService
import com.github.radlance.autodispatch.util.claimByNameOrUnauthorized
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.requests(service: RequestService) {
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

                val paginatedRequests = service.getRequests(
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
                val filters = service.getFilters()
                call.respond(HttpStatusCode.OK, filters)
            }

            get("/customers") {
                val query = call.request.queryParameters["q"] ?: ""
                val customers = service.getCustomers(query)
                call.respond(HttpStatusCode.OK, customers)
            }

            post {
                val login = call.claimByNameOrUnauthorized<String>("login")
                val request = call.receive<CreateRequest>()
                service.createRequest(login, request)
                call.respond(HttpStatusCode.Created)
            }

            put("/{id}") {
                val login = call.claimByNameOrUnauthorized<String>("login")
                val id = call.parameters["id"]?.toIntOrNull()
                    ?: return@put call.respond(HttpStatusCode.BadRequest, "Invalid request ID")
                val request = call.receive<CreateRequest>()

                service.editRequest(login, id, request)
                call.respond(HttpStatusCode.OK)
            }

            put("/{id}/cancel") {
                val id = call.parameters["id"]?.toIntOrNull()
                    ?: return@put call.respond(HttpStatusCode.BadRequest, "Invalid request ID")

                service.cancelAssignment(id)
                call.respond(HttpStatusCode.OK)
            }

            delete("/{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                    ?: return@delete call.respond(HttpStatusCode.BadRequest, "Invalid request ID")

                service.removeRequest(id)
                call.respond(HttpStatusCode.OK)
            }

            post("/{id}/assign") {
                val id = call.parameters["id"]?.toIntOrNull()
                    ?: return@post call.respond(HttpStatusCode.BadRequest, "Invalid request ID")
                val body = call.receive<AssignRequest>()

                service.assignDriver(id, body.driverId)
                call.respond(HttpStatusCode.Created)
            }

            put("/{id}/assign") {
                val id = call.parameters["id"]?.toIntOrNull()
                    ?: return@put call.respond(HttpStatusCode.BadRequest, "Invalid request ID")
                val body = call.receive<AssignRequest>()

                service.reassignDriver(id, body.driverId)
                call.respond(HttpStatusCode.OK)
            }

            get("/available") {
                val queryParams = call.request.queryParameters

                val page = queryParams["page"]?.toIntOrNull() ?: 1
                val pageSize = queryParams["pageSize"]?.toIntOrNull() ?: 3
                val searchQuery = queryParams["search"]

                val paginatedResult = service.getAvailableRequests(page, pageSize, searchQuery)
                call.respond(HttpStatusCode.OK, paginatedResult)
            }

            delete("/{id}/assign") {
                val id = call.parameters["id"]?.toIntOrNull()
                    ?: return@delete call.respond(HttpStatusCode.BadRequest, "Invalid request ID")

                service.unassignDriver(id)
                call.respond(HttpStatusCode.OK)
            }
        }
    }
}