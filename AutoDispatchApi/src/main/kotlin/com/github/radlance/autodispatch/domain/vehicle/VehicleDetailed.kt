package com.github.radlance.autodispatch.domain.vehicle

import kotlinx.serialization.Serializable

@Serializable
data class VehicleDetailed(
    val id: Int,
    val model: String,
    val licensePlate: String,
    val regionCode: String,
    val payloadCapacity: Int,
    val driverFullName: String?
)
