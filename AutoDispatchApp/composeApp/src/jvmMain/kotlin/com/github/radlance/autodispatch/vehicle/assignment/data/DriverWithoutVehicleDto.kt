package com.github.radlance.autodispatch.vehicle.assignment.data

import kotlinx.serialization.Serializable

@Serializable
data class DriverWithoutVehicleDto(
    val id: Int,
    val fullName: String,
    val phoneNumber: String,
    val totalDeliveries: Int
)
