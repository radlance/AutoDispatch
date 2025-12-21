package com.github.radlance.autodispatch.database.table

import org.jetbrains.exposed.dao.id.IntIdTable

object VehicleTable : IntIdTable(name = "vehicle") {
    val model = varchar(name = "model", length = 100)
    val licensePlate = varchar(name = "license_plate", length = 20)
    val payloadCapacity = integer(name = "payload_capacity")
}