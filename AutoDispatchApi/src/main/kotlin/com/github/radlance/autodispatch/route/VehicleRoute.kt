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
            get("/unassigned") {
                val stats = repository.unassignedVehicles()
                call.respond(HttpStatusCode.OK, stats)
            }

            put("/{id}/assign") {
                val id = call.parameters["id"]?.toIntOrNull()
                    ?: return@put call.respond(HttpStatusCode.BadRequest, "Invalid vehicle ID")

                val body = call.receive<AssignRequest>()

                repository.setDriverVehicle(
                    vehicleId = id,
                    driverId = body.driverId
                )

                call.respond(HttpStatusCode.OK)
            }
        }
    }
}