package com.github.radlance.autodispatch.route

import com.github.radlance.autodispatch.repository.AdminRepository
import com.github.radlance.autodispatch.util.claimByNameOrUnauthorized
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.admin(repository: AdminRepository) {
    authenticate {
        route("/admin") {
            get("/users") {
                val login = call.claimByNameOrUnauthorized<String>("login")
                val queryParams = call.request.queryParameters

                val page = queryParams["page"]?.toIntOrNull() ?: 1
                val pageSize = queryParams["pageSize"]?.toIntOrNull() ?: 20
                val searchQuery = queryParams["search"]

                fun parseIds(paramName: String): List<Int> {
                    return queryParams[paramName]
                        ?.split(',')
                        ?.mapNotNull { it.trim().toIntOrNull() }
                        ?: emptyList()
                }

                val statusIds = parseIds("statusIds")
                val roleIds = parseIds("roleIds")

                val paginatedUsers = repository.users(
                    page = page,
                    pageSize = pageSize,
                    searchQuery = searchQuery,
                    statusIds = statusIds,
                    roleIds = roleIds,
                    excludeLogin = login
                )

                call.respond(HttpStatusCode.OK, paginatedUsers)
            }

            get("/filters") {
                val filters = repository.filters()
                call.respond(HttpStatusCode.OK, filters)
            }
        }
    }
}