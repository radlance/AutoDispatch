package com.github.radlance.autodispatch.database.table

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.timestampWithTimeZone

object UserTable : IntIdTable(name = "users") {
    val login = varchar(name = "login", length = 50)
    val email = varchar(name = "email", length = 255).uniqueIndex()
    val passwordHash = varchar(name = "password_hash", length = 255)
    val salt = varchar(name = "salt", length = 255)
    val fullName = varchar(name = "full_name", length = 100)
    val avatarUrl = varchar(name = "avatar_url", length = 512).nullable()
    val phoneNumber = varchar(name = "phone_number", length = 20)
    val roleId = integer(name = "role_id")
    val createdAt = timestampWithTimeZone(name = "created_at").nullable()
}