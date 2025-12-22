package com.github.radlance.autodispatch.route

import com.github.radlance.autodispatch.domain.request.AssignRequest
import com.github.radlance.autodispatch.domain.request.CreateRequest
import com.github.radlance.autodispatch.repository.RequestRepository
import com.github.radlance.autodispatch.util.claimByNameOrUnauthorized
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.requests(repository: RequestRepository) {
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

                val paginatedRequests = repository.requests(
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
                val filters = repository.filters()
                call.respond(HttpStatusCode.OK, filters)
            }

            get("/customers") {
                val query = call.request.queryParameters["q"] ?: ""
                val customers = repository.customers(query = query)
                call.respond(HttpStatusCode.OK, customers)
            }

            post {
                val login = call.claimByNameOrUnauthorized<String>(name = "login")
                val request = call.receive<CreateRequest>()
                repository.createRequest(createdByLogin = login, req = request)
                call.respond(HttpStatusCode.Created)
            }

            put("/{id}") {
                val login = call.claimByNameOrUnauthorized<String>(name = "login")

                val id = call.parameters["id"]?.toIntOrNull()
                    ?: return@put call.respond(HttpStatusCode.BadRequest, "Invalid request ID")

                val createRequest = call.receive<CreateRequest>()

                repository.editRequest(
                    createdByLogin = login,
                    requestId = id,
                    req = createRequest
                )

                call.respond(HttpStatusCode.OK)
            }

            put("/{id}/cancel") {
                val id = call.parameters["id"]?.toIntOrNull()
                    ?: return@put call.respond(HttpStatusCode.BadRequest, "Invalid request ID")

                repository.cancelAssignment(requestId = id)
                call.respond(HttpStatusCode.OK)
            }

            delete("/{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                    ?: return@delete call.respond(HttpStatusCode.BadRequest, "Invalid request ID")

                repository.removeRequest(requestId = id)
                call.respond(HttpStatusCode.OK)
            }

            post("/{id}/assign") {
                val id = call.parameters["id"]?.toIntOrNull()
                    ?: return@post call.respond(HttpStatusCode.BadRequest, "Invalid request ID")

                val body = call.receive<AssignRequest>()

                repository.assignRequestToDriver(
                    requestId = id,
                    driverId = body.driverId
                )

                call.respond(HttpStatusCode.Created)
            }

            put("/{id}/assign") {
                val id = call.parameters["id"]?.toIntOrNull()
                    ?: return@put call.respond(HttpStatusCode.BadRequest, "Invalid request ID")

                val body = call.receive<AssignRequest>()

                repository.reassignRequestToDriver(
                    requestId = id,
                    driverId = body.driverId
                )

                call.respond(HttpStatusCode.OK)
            }

            get("/available") {
                val queryParams = call.request.queryParameters

                val page = queryParams["page"]?.toIntOrNull() ?: 1
                val pageSize = queryParams["pageSize"]?.toIntOrNull() ?: 3
                val searchQuery = queryParams["search"]

                val paginatedResult = repository.availableRequests(
                    page = page,
                    pageSize = pageSize,
                    searchQuery = searchQuery
                )

                call.respond(HttpStatusCode.OK, paginatedResult)
            }
        }
    }
}