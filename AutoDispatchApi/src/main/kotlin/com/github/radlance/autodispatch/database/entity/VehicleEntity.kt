package com.github.radlance.autodispatch.database.entity

import com.github.radlance.autodispatch.database.table.VehicleTable
import com.github.radlance.autodispatch.domain.request.Vehicle
import com.github.radlance.autodispatch.domain.request.VehicleFilter
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class VehicleEntity(id: EntityID<Int>) : IntEntity(id) {

    companion object : IntEntityClass<VehicleEntity>(VehicleTable)

    val model by VehicleTable.model
    val licencePlate by VehicleTable.licensePlate
    val year by VehicleTable.year
    val mileage by VehicleTable.mileage
    val fuelType by VehicleTable.fuelType
    val status by VehicleTable.status
    val lastServiceDate by VehicleTable.lastServiceDate
    val isActive by VehicleTable.isActive

    fun toVehicle(): Vehicle {
        return Vehicle(
            id = id.value,
            model = model,
            licencePlate = licencePlate,
            year = year,
            mileage = mileage,
            fuelType = fuelType,
            status = status,
            lastServiceDate = lastServiceDate?.toString(),
            isActive = isActive ?: false
        )
    }

    fun toVehicleFilter(): VehicleFilter {
        return VehicleFilter(
            id = id.value,
            model = model,
            licencePlate = licencePlate
        )
    }
}