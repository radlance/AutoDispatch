package com.github.radlance.autodispatch.route

import com.github.radlance.autodispatch.domain.request.AssignRequest
import com.github.radlance.autodispatch.repository.VehicleRepository
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.vehicle(repository: VehicleRepository) {
    authenticate {
        route("/vehicles") {
            get {
                val queryParams = call.request.queryParameters
                val page = queryParams["page"]?.toIntOrNull() ?: 1
                val pageSize = queryParams["pageSize"]?.toIntOrNull() ?: 20
                val searchQuery = queryParams["search"]

                val paginatedRequests = repository.vehicles(
                    page = page,
                    pageSize = pageSize,
                    searchQuery = searchQuery
                )

                call.respond(HttpStatusCode.OK, paginatedRequests)
            }

            get("/unassigned") {
                val queryParams = call.request.queryParameters
                val page = queryParams["page"]?.toIntOrNull() ?: 1
                val pageSize = queryParams["pageSize"]?.toIntOrNull() ?: 20
                val searchQuery = queryParams["search"]

                val stats = repository.unassignedVehicles(
                    page = page,
                    pageSize = pageSize,
                    searchQuery = searchQuery
                )
                call.respond(HttpStatusCode.OK, stats)
            }

            post("/{id}/assignment") {
                val id = call.parameters["id"]?.toIntOrNull()
                    ?: return@post call.respond(HttpStatusCode.BadRequest, "Invalid vehicle ID")

                val body = call.receive<AssignRequest>()

                repository.assignDriverVehicle(
                    vehicleId = id,
                    driverId = body.driverId
                )

                call.respond(HttpStatusCode.OK)
            }

            patch("/{id}/assignment") {
                val id = call.parameters["id"]?.toIntOrNull()
                    ?: return@patch call.respond(HttpStatusCode.BadRequest, "Invalid vehicle ID")

                val body = call.receive<AssignRequest>()

                repository.reassignDriverVehicle(
                    vehicleId = id,
                    driverId = body.driverId
                )

                call.respond(HttpStatusCode.OK)
            }
        }
    }
}