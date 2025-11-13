package com.github.radlance.autodispatch.route

import com.github.radlance.autodispatch.service.DeliveryService
import com.github.radlance.autodispatch.util.claimByNameOrUnauthorized
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.deliveries(deliveryService: DeliveryService) {
    authenticate {
        route("/deliveries") {
            get {
                val login = call.claimByNameOrUnauthorized<String>(name = "login")
                val deliveries = deliveryService.deliveries(login)
                call.respond(HttpStatusCode.OK, deliveries)
            }

            get("/{id}/details") {
                val id = call.parameters["id"]?.toIntOrNull()
                    ?: return@get call.respond(HttpStatusCode.BadRequest, "Invalid delivery ID")
                val login = call.claimByNameOrUnauthorized<String>(name = "login")

                val delivery = deliveryService.delivery(login, id)

                call.respond(HttpStatusCode.OK, delivery)
            }

            post("/{id}/start") {
                val id = call.parameters["id"]?.toIntOrNull()
                    ?: return@post call.respond(HttpStatusCode.BadRequest, "Invalid delivery ID")
                val login = call.claimByNameOrUnauthorized<String>("login")

                deliveryService.startDelivery(id, login)
                call.respond(HttpStatusCode.OK)
            }
        }
    }
}