package com.github.radlance.autodispatch.request.assignment.data

import kotlinx.serialization.Serializable

@Serializable
data class VehicleStatsDto(
    val id: Int,
    val model: String,
    val licencePlate: String,
    val vehicleStatus: String
)
