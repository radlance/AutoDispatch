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
                val requests = requestService.requests()
                call.respond(HttpStatusCode.OK, requests)
            }
        }
    }
}