package com.github.radlance.autodispatch.database.table

import org.jetbrains.exposed.dao.id.IntIdTable

object UserStatusTable : IntIdTable("user_status") {
    val name = varchar("name", 64)
}