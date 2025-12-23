package com.github.radlance.autodispatch.route

import com.github.radlance.autodispatch.repository.DeliveryRepository
import com.github.radlance.autodispatch.util.claimByNameOrUnauthorized
import com.github.radlance.autodispatch.util.fileUploadDir
import com.github.radlance.autodispatch.util.processImagesUpload
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.deliveries(repository: DeliveryRepository) {
    val uploadDir = fileUploadDir
    if (!uploadDir.exists()) uploadDir.mkdirs()

    authenticate {
        route("/deliveries") {
            get {
                val login = call.claimByNameOrUnauthorized<String>("login")
                val queryParams = call.request.queryParameters
                val page = queryParams["page"]?.toIntOrNull() ?: 1
                val pageSize = queryParams["pageSize"]?.toIntOrNull() ?: 5
                val searchQuery = queryParams["search"]

                val paginatedResult = repository.deliveries(
                    driverLogin = login,
                    searchQuery = searchQuery,
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

                call.processImagesUpload(uploadDir) { photoUrls ->
                    repository.uploadDeliveryDocuments(id, login, photoUrls)
                }
            }

            post("/{id}/retake-documents") {
                val id = call.parameters["id"]?.toIntOrNull()
                    ?: return@post call.respond(HttpStatusCode.BadRequest, "Invalid delivery ID")

                val login = call.claimByNameOrUnauthorized<String>("login")

                call.processImagesUpload(uploadDir) { photoUrls ->
                    repository.retakeDeliveryDocuments(id, login, photoUrls)
                }
            }

            get("/history/my") {
                val login = call.claimByNameOrUnauthorized<String>("login")
                val queryParams = call.request.queryParameters
                val page = queryParams["page"]?.toIntOrNull() ?: 1
                val pageSize = queryParams["pageSize"]?.toIntOrNull() ?: 5
                val searchQuery = queryParams["search"]

                val paginatedResult = repository.deliveryHistory(
                    driverLogin = login,
                    searchQuery = searchQuery,
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
                val searchQuery = queryParams["search"]

                val paginatedResult = repository.driverDeliveryHistory(
                    driverId = id,
                    searchQuery = searchQuery,
                    pageSize = pageSize,
                    page = page
                )

                call.respond(HttpStatusCode.OK, paginatedResult)
            }
        }
    }
}