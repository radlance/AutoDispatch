package com.github.radlance.autodispatch.database.entity

import com.github.radlance.autodispatch.database.table.CityTable
import com.github.radlance.autodispatch.domain.request.City
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class CityEntity(id: EntityID<Int>) : IntEntity(id) {

    companion object : IntEntityClass<CityEntity>(CityTable)

    val name by CityTable.name

    fun toCity(): City {
        return City(id = id.value, name = name)
    }
}