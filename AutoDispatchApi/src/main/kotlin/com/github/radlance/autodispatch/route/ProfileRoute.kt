package com.github.radlance.autodispatch.route

import com.github.radlance.autodispatch.service.ProfileService
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.profile(profileService: ProfileService) {
    authenticate {
        route("/users") {
            get("/{login}") {
                val login = call.parameters["login"]
                    ?: run {
                        call.respond(HttpStatusCode.BadRequest, "Login is missing")
                        return@get
                    }

                val user = profileService.userByLogin(login)
                call.respond(HttpStatusCode.OK, user)
            }
        }
    }
}