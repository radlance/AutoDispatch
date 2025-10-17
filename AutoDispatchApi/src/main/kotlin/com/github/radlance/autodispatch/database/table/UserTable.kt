package com.github.radlance.autodispatch.database.table

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.timestamp

object UserTable : IntIdTable(name = "users") {
    val login = varchar(name = "login", length = 50)
    val passwordHash = varchar(name = "password_hash", length = 255)
    val salt = varchar(name = "salt", length = 255)
    val fullName = varchar(name = "full_name", length = 100)
    val phoneNumber = varchar(name = "phone_number", length = 20)
    val role = varchar(name = "role", length = 20)
    val isActive = bool(name = "is_active").nullable()
    val createdAt = timestamp(name = "created_at").nullable()
}