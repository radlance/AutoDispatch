package com.github.radlance.autodispatch.database.table

import org.jetbrains.exposed.dao.id.IntIdTable

object CityTable : IntIdTable(name = "city") {
    val name = varchar(name = "name", length = 255)
}