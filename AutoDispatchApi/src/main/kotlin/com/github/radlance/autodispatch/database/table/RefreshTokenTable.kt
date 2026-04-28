package com.github.radlance.autodispatch.database.table

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.timestamp

object RefreshTokenTable : Table("refresh_token") {
    val token = varchar("token", 255)
    val userId = reference("user_id", UserTable)
    val expiresAt = timestamp("expires_at")
    val createdAt = timestamp("created_at")

    override val primaryKey = PrimaryKey(token)
}