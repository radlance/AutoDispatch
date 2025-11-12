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

fun Route.requests(requestService: RequestService) {
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
                val filters = requestService.filters()
                call.respond(HttpStatusCode.OK, filters)
            }

            get("/customers") {
                val query = call.request.queryParameters["q"] ?: ""
                val customers = requestService.customers(query = query)
                call.respond(HttpStatusCode.OK, customers)
            }

            post {
                val login = call.claimByNameOrUnauthorized<String>(name = "login")
                val request = call.receive<CreateRequest>()
                requestService.createRequest(login = login, request = request)
                call.respond(HttpStatusCode.Created)
            }

            put("/{id}") {
                val login = call.claimByNameOrUnauthorized<String>(name = "login")

                val id = call.parameters["id"]?.toIntOrNull()
                    ?: return@put call.respond(HttpStatusCode.BadRequest, "Invalid request ID")

                val createRequest = call.receive<CreateRequest>()

                requestService.editRequest(
                    login = login,
                    requestId = id,
                    request = createRequest
                )

                call.respond(HttpStatusCode.OK)
            }

            patch("/{id}/cancel") {
                val id = call.parameters["id"]?.toIntOrNull()
                    ?: return@patch call.respond(HttpStatusCode.BadRequest, "Invalid request ID")

                requestService.cancelRequest(requestId = id)
                call.respond(HttpStatusCode.OK)
            }

            patch("/{id}/assignment/cancel") {
                val id = call.parameters["id"]?.toIntOrNull()
                    ?: return@patch call.respond(HttpStatusCode.BadRequest, "Invalid request ID")
                requestService.cancelAssignment(requestId = id)
                call.respond(HttpStatusCode.OK)
            }

            get("/request-assignment") {
                val assignment = requestService.requestAssignment()
                call.respond(HttpStatusCode.OK, assignment)
            }

            post("/{id}/assign") {
                val id = call.parameters["id"]?.toIntOrNull()
                    ?: return@post call.respond(HttpStatusCode.BadRequest, "Invalid request ID")

                val body = call.receive<AssignRequest>()

                requestService.assignRequestToDriver(
                    requestId = id,
                    driverId = body.driverId
                )

                call.respond(HttpStatusCode.Created)
            }

            put("/{id}/assign") {
                val id = call.parameters["id"]?.toIntOrNull()
                    ?: return@put call.respond(HttpStatusCode.BadRequest, "Invalid request ID")

                val body = call.receive<AssignRequest>()

                requestService.reAssignRequestToDriver(
                    requestId = id,
                    driverId = body.driverId
                )

                call.respond(HttpStatusCode.OK)
            }
        }
    }
}