package com.github.radlance.autodispatch.route

import com.github.radlance.autodispatch.domain.document.RejectDocumentDto
import com.github.radlance.autodispatch.repository.DocumentsRepository
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.documentRoute(repository: DocumentsRepository) {
    authenticate {
        route("/documents") {
            post("/{id}/reject") {
                val id = call.parameters["id"]?.toIntOrNull()
                    ?: return@post call.respond(HttpStatusCode.BadRequest, "Invalid request ID")
                val body = call.receive<RejectDocumentDto>()

                repository.rejectDocument(requestId = id, rejectDocumentDto = body)
                call.respond(HttpStatusCode.OK)
            }

            post("/{id}/approve") {
                val id = call.parameters["id"]?.toIntOrNull()
                    ?: return@post call.respond(HttpStatusCode.BadRequest, "Invalid request ID")

                repository.approveDocument(requestId = id)
                call.respond(HttpStatusCode.OK)
            }
        }
    }
}