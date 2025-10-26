package com.github.radlance.autodispatch.database.entity

import com.github.radlance.autodispatch.database.table.CargoTypeTable
import com.github.radlance.autodispatch.domain.request.CargoType
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class CargoTypeEntity(id: EntityID<Int>) : IntEntity(id) {

    companion object : IntEntityClass<CargoTypeEntity>(CargoTypeTable)

    val name by CargoTypeTable.name

    fun toCargoType(): CargoType {
        return CargoType(id = id.value, name = name)
    }
}