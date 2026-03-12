package com.github.radlance.autodispatch.request.core.data

import kotlinx.serialization.Serializable

@Serializable
data class VehicleDto(
    val id: Int,
    val model: String,
    val licensePlate: String,
    val regionCode: String,
    val payloadCapacity: Int
)
