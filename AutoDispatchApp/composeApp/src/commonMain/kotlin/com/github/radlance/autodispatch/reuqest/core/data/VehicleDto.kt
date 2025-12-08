package com.github.radlance.autodispatch.reuqest.core.data

import kotlinx.serialization.Serializable

@Serializable
data class VehicleDto(
    val id: Int,
    val model: String,
    val licensePlate: String,
    val payloadCapacity: Int
)
