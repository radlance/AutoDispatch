package com.github.radlance.autodispatch.vehicle.core.domain

data class VehicleDetailed(
    val id: Int,
    val model: String,
    val licensePlate: String,
    val payloadCapacity: Int,
    val driverFullName: String?
)
