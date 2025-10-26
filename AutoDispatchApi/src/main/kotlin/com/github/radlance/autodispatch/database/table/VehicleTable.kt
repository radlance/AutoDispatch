package com.github.radlance.autodispatch.database.table

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.timestamp

object VehicleTable : IntIdTable(name = "vehicle") {
    val model = varchar(name = "model", length = 100)
    val licensePlate = varchar(name = "license_plate", length = 20)
    val year = integer(name = "year")
    val mileage = integer(name = "mileage")
    val fuelType = varchar(name = "fuel_type", length = 20)
    val status = varchar(name = "status", length = 20)
    val lastServiceDate = timestamp(name = "last_service_date").nullable()
    val isActive = bool(name = "is_active").nullable()
}