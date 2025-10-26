package com.github.radlance.autodispatch.database.table

import org.jetbrains.exposed.dao.id.IntIdTable

object RequestStatusTable : IntIdTable(name = "request_status") {
    val name = varchar(name = "name", length = 255)
}