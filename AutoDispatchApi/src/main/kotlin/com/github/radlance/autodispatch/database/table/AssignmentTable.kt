package com.github.radlance.autodispatch.database.table

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.timestamp

object AssignmentTable : IntIdTable(name = "assignment") {
    val requestId = reference("request_id", RequestTable)
    val driverId = reference("driver_id", UserTable)
    val assignedAt = timestamp("assigned_at").nullable()
    val startedAt = timestamp("started_at").nullable()
    val completedAt = timestamp("completed_at").nullable()

    init {
        uniqueIndex(requestId)
    }
}