package com.github.radlance.autodispatch.route

import com.github.radlance.autodispatch.service.DeliveryService
import com.github.radlance.autodispatch.util.claimByNameOrUnauthorized
import com.github.radlance.autodispatch.util.fileUploadDir
import com.github.radlance.autodispatch.util.processImagesUpload
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.deliveries(service: DeliveryService) {

    val uploadDir = fileUploadDir
    if (!uploadDir.exists()) uploadDir.mkdirs()

    get("/open-delivery/{id}") {
        val id = call.parameters["id"]
        val number = call.request.queryParameters["number"] ?: ""

        if (id != null) {
            val deepLink = "autodispatch://requests/$id?number=$number"

            call.response.header(HttpHeaders.Location, deepLink)
            call.respond(HttpStatusCode.Found)
        } else {
            call.respond(HttpStatusCode.BadRequest, "Missing ID")
        }
    }

    authenticate {
        route("/deliveries") {

            get {
                val login = call.claimByNameOrUnauthorized<String>("login")
                val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
                val pageSize = call.request.queryParameters["pageSize"]?.toIntOrNull() ?: 5
                val search = call.request.queryParameters["search"]

                call.respond(
                    HttpStatusCode.OK,
                    service.deliveries(
                        driverLogin = login,
                        searchQuery = search,
                        page = page,
                        pageSize = pageSize
                    )
                )
            }

            get("/{id}/details") {
                val id = call.parameters["id"]?.toIntOrNull()
                    ?: return@get call.respond(HttpStatusCode.BadRequest, "Invalid delivery ID")

                val login = call.claimByNameOrUnauthorized<String>("login")
                call.respond(HttpStatusCode.OK, service.delivery(login, id))
            }

            post("/{id}/start") {
                val id = call.parameters["id"]?.toIntOrNull()
                    ?: return@post call.respond(HttpStatusCode.BadRequest, "Invalid delivery ID")

                val login = call.claimByNameOrUnauthorized<String>("login")
                service.startDelivery(id, login)

                call.respond(HttpStatusCode.OK)
            }

            post("/{id}/upload-documents") {
                val id = call.parameters["id"]?.toIntOrNull()
                    ?: return@post call.respond(HttpStatusCode.BadRequest, "Invalid delivery ID")

                val login = call.claimByNameOrUnauthorized<String>("login")

                call.processImagesUpload(uploadDir) { urls ->
                    service.uploadDocuments(id, login, urls)
                }

                call.respond(HttpStatusCode.OK)
            }

            post("/{id}/retake-documents") {
                val id = call.parameters["id"]?.toIntOrNull()
                    ?: return@post call.respond(HttpStatusCode.BadRequest, "Invalid delivery ID")

                val login = call.claimByNameOrUnauthorized<String>("login")

                call.processImagesUpload(uploadDir) { urls ->
                    service.retakeDocuments(id, login, urls)
                }

                call.respond(HttpStatusCode.OK)
            }

            get("/history/my") {
                val login = call.claimByNameOrUnauthorized<String>("login")
                val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
                val pageSize = call.request.queryParameters["pageSize"]?.toIntOrNull() ?: 5
                val search = call.request.queryParameters["search"]

                call.respond(
                    HttpStatusCode.OK,
                    service.deliveryHistory(
                        driverLogin = login,
                        searchQuery = search,
                        pageSize = pageSize,
                        page = page
                    )
                )
            }

            get("/history/{driverId}") {
                val id = call.parameters["driverId"]?.toIntOrNull()
                    ?: return@get call.respond(HttpStatusCode.BadRequest, "Invalid driver ID")
                val queryParams = call.request.queryParameters
                val page = queryParams["page"]?.toIntOrNull() ?: 1
                val pageSize = queryParams["pageSize"]?.toIntOrNull() ?: 5
                val searchQuery = queryParams["search"]

                val paginatedResult = service.driverDeliveryHistory(
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
