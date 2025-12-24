package com.github.radlance.autodispatch.route

import com.github.radlance.autodispatch.repository.ProfileRepository
import com.github.radlance.autodispatch.util.claimByNameOrUnauthorized
import com.github.radlance.autodispatch.util.fileUploadDir
import com.github.radlance.autodispatch.util.processSingleImageUpload
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.profile(repository: ProfileRepository) {
    val uploadDir = fileUploadDir
    if (!uploadDir.exists()) uploadDir.mkdirs()

    authenticate {
        route("/profile") {
            get {
                val userLogin = call.claimByNameOrUnauthorized<String>(name = "login")

                val user = repository.profile(userLogin)
                call.respond(HttpStatusCode.OK, user)
            }

            get("/details") {
                val userLogin = call.claimByNameOrUnauthorized<String>(name = "login")

                val details = repository.profileDetails(userLogin)
                call.respond(HttpStatusCode.OK, details)
            }

            post("/avatar") {
                val login = call.claimByNameOrUnauthorized<String>("login")

                call.processSingleImageUpload(fileUploadDir) { imageUrl ->
                    repository.uploadAvatar(login, imageUrl)
                }
            }

            delete("/avatar") {
                val userLogin = call.claimByNameOrUnauthorized<String>(name = "login")

                repository.deleteAvatar(userLogin)
                call.respond(HttpStatusCode.OK)
            }
        }
    }
}