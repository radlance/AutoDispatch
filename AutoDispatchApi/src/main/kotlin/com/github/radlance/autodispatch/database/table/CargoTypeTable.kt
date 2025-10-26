package com.github.radlance.autodispatch.database.table

import org.jetbrains.exposed.dao.id.IntIdTable

object CargoTypeTable : IntIdTable(name = "cargo_type") {
    val name = varchar(name = "name", length = 100)
}