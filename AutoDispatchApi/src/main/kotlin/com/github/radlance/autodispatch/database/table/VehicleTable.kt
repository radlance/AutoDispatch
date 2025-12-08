package com.github.radlance.autodispatch.database.table

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.timestampWithTimeZone

object VehicleTable : IntIdTable(name = "vehicle") {
    val model = varchar(name = "model", length = 100)
    val licensePlate = varchar(name = "license_plate", length = 20)
    val year = integer(name = "year")
    val mileage = integer(name = "mileage")
    val fuelType = varchar(name = "fuel_type", length = 20)
    val statusId = reference(name = "status_id", VehicleStatusTable)
    val lastServiceDate = timestampWithTimeZone(name = "last_service_date").nullable()
    val isActive = bool(name = "is_active").nullable()
    val payloadCapacity = integer(name = "payload_capacity")
}