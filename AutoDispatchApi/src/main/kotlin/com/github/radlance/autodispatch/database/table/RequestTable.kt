package com.github.radlance.autodispatch.database.table

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.timestamp

object RequestTable : IntIdTable(name = "request") {
    val createdById = reference("created_by_id", UserTable)
    val statusId = reference("status_id", RequestStatusTable)
    val loadingPoint = varchar("loading_point", 255)
    val unloadingPoint = varchar("unloading_point", 255)
    val cargoTypeId = reference("cargo_type_id", CargoTypeTable).nullable()
    val cargoWeight = double("cargo_weight")
    val cargoVolume = double("cargo_volume")
    val cargoDescription = text("cargo_description").nullable()
    val customerId = reference("customer_id", CustomerTable).nullable()
    val startedTripAt = timestamp("started_trip_at").nullable()
    val endedTripAt = timestamp("ended_trip_at").nullable()
    val createdAt = timestamp("created_at").nullable()
    val originId = reference("origin_id", CityTable).nullable()
    val destinationId = reference("destination_id", CityTable).nullable()
    val requestNumber = varchar("request_number", 6).nullable()
}