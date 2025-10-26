package com.github.radlance.autodispatch.database.table

import org.jetbrains.exposed.dao.id.IntIdTable

object RoleTable : IntIdTable(name = "role") {
    val name = varchar(name = "name", length = 64)
}