package com.github.radlance.autodispatch.database.table

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.timestampWithTimeZone

object AssignmentTable : IntIdTable(name = "assignment") {
    val requestId = reference("request_id", RequestTable)
    val driverId = reference("driver_id", UserTable)
    val vehicleId = reference("vehicle_id", VehicleTable)
    val assignedAt = timestampWithTimeZone("assigned_at").nullable()
    val startedAt = timestampWithTimeZone("started_at").nullable()
    val completedAt = timestampWithTimeZone("completed_at").nullable()

    init {
        uniqueIndex(requestId)
    }
}