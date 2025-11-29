package com.github.radlance.autodispatch.route

import com.github.radlance.autodispatch.repository.ProfileRepository
import com.github.radlance.autodispatch.util.claimByNameOrUnauthorized
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.profile(repository: ProfileRepository) {
    authenticate {
        route("/users") {
            get {
                val userLogin = call.claimByNameOrUnauthorized<String>(name = "login")

                val user = repository.userByLogin(userLogin)
                call.respond(HttpStatusCode.OK, user)
            }
        }
    }
}