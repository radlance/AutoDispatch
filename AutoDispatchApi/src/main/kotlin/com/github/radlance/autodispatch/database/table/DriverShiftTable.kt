package com.github.radlance.autodispatch.database.table

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.time
import org.jetbrains.exposed.sql.javatime.timestampWithTimeZone

object DriverShiftTable : IntIdTable(name = "driver_shift") {
    val driverId = reference("driver_id", UserTable)
    val dayOfWeek = short("day_of_week")
    val startTime = time("start_time")
    val endTime = time("end_time")
    val createdAt = timestampWithTimeZone("created_at").nullable()
}