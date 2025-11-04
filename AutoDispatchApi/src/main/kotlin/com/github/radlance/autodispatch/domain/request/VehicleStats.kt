package com.github.radlance.autodispatch.domain.request

import kotlinx.serialization.Serializable

@Serializable
data class VehicleStats(
    val id: Int,
    val model: String,
    val licencePlate: String,
    val vehicleStatus: String
)
