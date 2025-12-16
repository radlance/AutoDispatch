package com.github.radlance.autodispatch.route

import com.github.radlance.autodispatch.repository.DeliveryRepository
import com.github.radlance.autodispatch.util.claimByNameOrUnauthorized
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.utils.io.jvm.javaio.*
import java.io.File
import java.util.*

fun Route.deliveries(repository: DeliveryRepository) {
    val mode = System.getenv("MODE") ?: "debug"

    val uploadDir = if (mode == "release") {
        File("/uploads")
    } else {
        File("uploaded_files")
    }

    if (!uploadDir.exists()) uploadDir.mkdirs()

    authenticate {
        route("/deliveries") {
            get {
                val login = call.claimByNameOrUnauthorized<String>("login")
                val queryParams = call.request.queryParameters
                val page = queryParams["page"]?.toIntOrNull() ?: 1
                val pageSize = queryParams["pageSize"]?.toIntOrNull() ?: 5

                val paginatedResult = repository.deliveries(
                    driverLogin = login,
                    page = page,
                    pageSie = pageSize
                )

                call.respond(HttpStatusCode.OK, paginatedResult)
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

            post("/{id}/upload-documents") {
                val id = call.parameters["id"]?.toIntOrNull()
                    ?: return@post call.respond(HttpStatusCode.BadRequest, "Invalid delivery ID")

                val login = call.claimByNameOrUnauthorized<String>("login")

                call.processDocumentUpload(uploadDir) { photoUrls ->
                    repository.uploadDeliveryDocuments(id, login, photoUrls)
                }
            }

            post("/{id}/retake-documents") {
                val id = call.parameters["id"]?.toIntOrNull()
                    ?: return@post call.respond(HttpStatusCode.BadRequest, "Invalid delivery ID")

                val login = call.claimByNameOrUnauthorized<String>("login")

                call.processDocumentUpload(uploadDir) { photoUrls ->
                    repository.retakeDeliveryDocuments(id, login, photoUrls)
                }
            }

            get("/history/my") {
                val login = call.claimByNameOrUnauthorized<String>("login")
                val queryParams = call.request.queryParameters
                val page = queryParams["page"]?.toIntOrNull() ?: 1
                val pageSize = queryParams["pageSize"]?.toIntOrNull() ?: 5

                val paginatedResult = repository.deliveryHistory(
                    driverLogin = login,
                    page = page,
                    pageSize = pageSize
                )

                call.respond(HttpStatusCode.OK, paginatedResult)
            }

            get("/history/{driverId}") {
                val id = call.parameters["driverId"]?.toIntOrNull()
                    ?: return@get call.respond(HttpStatusCode.BadRequest, "Invalid driver ID")
                val queryParams = call.request.queryParameters
                val page = queryParams["page"]?.toIntOrNull() ?: 1
                val pageSize = queryParams["pageSize"]?.toIntOrNull() ?: 5

                val paginatedResult = repository.driverDeliveryHistory(
                    driverId = id,
                    pageSize = pageSize,
                    page = page
                )

                call.respond(HttpStatusCode.OK, paginatedResult)
            }
        }
    }
}

private suspend fun handleFileUpload(
    call: ApplicationCall,
    uploadDir: File
): List<String> {
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

    return photoUrls
}

private fun deleteUploadedFiles(photoUrls: List<String>, uploadDir: File) {
    photoUrls.forEach { url ->
        val fileName = url.substringAfter("/static/")
        val file = File(uploadDir, fileName)
        if (file.exists()) file.delete()
    }
}

private suspend fun ApplicationCall.processDocumentUpload(
    uploadDir: File,
    block: suspend (photoUrls: List<String>) -> Unit
) {
    val photoUrls = handleFileUpload(this, uploadDir)

    try {
        block(photoUrls)
        respond(HttpStatusCode.OK)
    } catch (e: Exception) {
        deleteUploadedFiles(photoUrls, uploadDir)
        respond(HttpStatusCode.InternalServerError, e.message ?: "Unknown error")
    }
}