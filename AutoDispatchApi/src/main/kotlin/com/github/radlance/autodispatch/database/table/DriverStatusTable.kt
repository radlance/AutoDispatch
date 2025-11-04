package com.github.radlance.autodispatch.database.table

import org.jetbrains.exposed.dao.id.IntIdTable

object DriverStatusTable : IntIdTable(name = "driver_status") {
    val name = varchar(name = "name", length = 64)
}