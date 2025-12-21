package com.github.radlance.autodispatch.database.table

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.timestampWithTimeZone

object DriverTable : Table(name = "driver") {
    val userId = reference("user_id", UserTable)
    val statusId = reference("status_id", DriverStatusTable)
    val vehicleId = reference("vehicle_id", VehicleTable).nullable()
    val createdAt = timestampWithTimeZone("created_at").nullable()
    val updatedAt = timestampWithTimeZone("updated_at").nullable()

    override val primaryKey = PrimaryKey(userId)
}