package com.github.radlance.autodispatch.route

import com.github.radlance.autodispatch.repository.DeliveryRepository
import com.github.radlance.autodispatch.util.claimByNameOrUnauthorized
import io.ktor.http.*
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.server.auth.*
import io.ktor.server.request.receiveMultipart
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.utils.io.jvm.javaio.toInputStream
import java.io.File
import java.util.UUID

fun Route.deliveries(repository: DeliveryRepository) {
    val uploadDir = File("/uploads")
    if (!uploadDir.exists()) {
        uploadDir.mkdirs()
    }

    authenticate {
        route("/deliveries") {
            get {
                val login = call.claimByNameOrUnauthorized<String>(name = "login")
                val deliveries = repository.deliveries(login)
                call.respond(HttpStatusCode.OK, deliveries)
            }

            get("/{id}/details") {
                val id = call.parameters["id"]?.toIntOrNull()
                    ?: return@get call.respond(HttpStatusCode.BadRequest, "Invalid delivery ID")
                val login = call.claimByNameOrUnauthorized<String>(name = "login")

                val delivery = repository.delivery(login, id)

                call.respond(HttpStatusCode.OK, delivery)
            }

            post("/{id}/start") {
                val id = call.parameters["id"]?.toIntOrNull()
                    ?: return@post call.respond(HttpStatusCode.BadRequest, "Invalid delivery ID")
                val login = call.claimByNameOrUnauthorized<String>("login")

                repository.startDelivery(id, login)
                call.respond(HttpStatusCode.OK)
            }

            post("/{id}/complete") {
                val id = call.parameters["id"]?.toIntOrNull()
                    ?: return@post call.respond(HttpStatusCode.BadRequest, "Invalid delivery ID")

                val login = call.claimByNameOrUnauthorized<String>("login")

                val multipart = call.receiveMultipart()
                val photoUrls = mutableListOf<String>()

                multipart.forEachPart { part ->
                    if (part is PartData.FileItem) {
                        val extension = File(part.originalFileName ?: "file.jpg").extension
                        val fileName = "${UUID.randomUUID()}.$extension"
                        val file = File(uploadDir, fileName)

                        part.provider().toInputStream().use { input ->
                            file.outputStream().buffered().use { output ->
                                input.copyTo(output)
                            }
                        }

                        photoUrls.add("/static/$fileName")
                    }
                    part.dispose()
                }
                try {
                    repository.completeDelivery(deliveryId = id, driverLogin = login)

                    call.respond(HttpStatusCode.OK)
                } catch (e: Exception) {
                    photoUrls.forEach { url ->
                        val fileName = url.substringAfter("/static/")
                        val fileToDelete = File(uploadDir, fileName)

                        if (fileToDelete.exists()) {
                            fileToDelete.delete()
                        }
                    }
                    call.respond(HttpStatusCode.InternalServerError, e.message!!)
                }
            }
        }
    }
}