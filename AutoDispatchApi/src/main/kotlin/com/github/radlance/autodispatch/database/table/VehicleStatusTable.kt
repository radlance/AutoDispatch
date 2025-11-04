package com.github.radlance.autodispatch.database.table

import org.jetbrains.exposed.dao.id.IntIdTable

object VehicleStatusTable : IntIdTable(name = "vehicle_status") {
    val name = varchar(name = "name", length = 64)
}