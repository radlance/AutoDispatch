package com.github.radlance.autodispatch.database.table

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.timestampWithTimeZone

object RequestTable : IntIdTable(name = "request") {
    val createdById = reference("created_by_id", UserTable)
    val statusId = reference("status_id", RequestStatusTable)
    val loadingAddress = text("loading_address").nullable()
    val loadingLat = double("loading_lat")
    val loadingLon = double("loading_lon")
    val unloadingAddress = text("unloading_address").nullable()
    val unloadingLat = double("unloading_lat")
    val unloadingLon = double("unloading_lon")
    val cargoTypeId = reference("cargo_type_id", CargoTypeTable).nullable()
    val cargoWeight = double("cargo_weight")
    val cargoVolume = double("cargo_volume").nullable()
    val cargoDescription = text("cargo_description").nullable()
    val customerId = reference("customer_id", CustomerTable).nullable()
    val createdAt = timestampWithTimeZone("created_at").nullable()
    val updatedAt = timestampWithTimeZone("updated_at").nullable()
    val plannedLoadingAt = timestampWithTimeZone("planned_loading_at").nullable()
    val plannedUnloadingAt = timestampWithTimeZone("planned_unloading_at").nullable()
    val actualLoadingAt = timestampWithTimeZone("actual_loading_at").nullable()
    val actualUnloadingAt = timestampWithTimeZone("actual_unloading_at").nullable()
    val originId = reference("origin_id", CityTable).nullable()
    val destinationId = reference("destination_id", CityTable).nullable()
    val requestNumber = varchar("request_number", 6).nullable()
    val transportationDescription = text("transportation_description").nullable()
    val rejectionReason = text("rejection_reason").nullable()
}