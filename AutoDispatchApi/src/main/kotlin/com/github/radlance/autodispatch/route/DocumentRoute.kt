package com.github.radlance.autodispatch.route

import com.github.radlance.autodispatch.domain.document.RejectDocumentDto
import com.github.radlance.autodispatch.service.DocumentsService
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.documents(service: DocumentsService) {

    authenticate {
        route("/documents") {

            post("/{id}/approve") {
                val id = call.parameters["id"]?.toIntOrNull()
                    ?: return@post call.respond(HttpStatusCode.BadRequest, "Invalid request ID")

                service.approveDocuments(id)
                call.respond(HttpStatusCode.OK)
            }

            post("/{id}/reject") {
                val id = call.parameters["id"]?.toIntOrNull()
                    ?: return@post call.respond(HttpStatusCode.BadRequest, "Invalid request ID")

                val body = call.receive<RejectDocumentDto>()
                service.rejectDocuments(id, body.reason)

                call.respond(HttpStatusCode.OK)
            }
        }
    }
}
