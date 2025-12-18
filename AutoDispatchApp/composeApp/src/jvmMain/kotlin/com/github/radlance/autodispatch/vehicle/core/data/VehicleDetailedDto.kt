package com.github.radlance.autodispatch.vehicle.core.data

import kotlinx.serialization.Serializable

@Serializable
data class VehicleDetailedDto(
    val id: Int,
    val model: String,
    val licensePlate: String,
    val payloadCapacity: Int,
    val driverFullName: String?
)
