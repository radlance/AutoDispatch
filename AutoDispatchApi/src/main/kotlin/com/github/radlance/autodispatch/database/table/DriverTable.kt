package com.github.radlance.autodispatch.database.table

import org.jetbrains.exposed.sql.Table

object DriverTable : Table(name = "driver") {
    val userId = reference("user_id", UserTable)
    val statusId = reference("status_id", DriverStatusTable)

    override val primaryKey = PrimaryKey(userId)
}