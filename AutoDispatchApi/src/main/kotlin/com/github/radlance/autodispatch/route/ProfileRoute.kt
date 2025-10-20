package com.github.radlance.autodispatch.route

import com.github.radlance.autodispatch.service.ProfileService
import com.github.radlance.autodispatch.util.claimByNameOrUnauthorized
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.profile(profileService: ProfileService) {
    authenticate {
        route("/users") {
            get("/") {
                val userLogin = call.claimByNameOrUnauthorized<String>(name = "login")

                val user = profileService.userByLogin(userLogin)
                call.respond(HttpStatusCode.OK, user)
            }
        }
    }
}