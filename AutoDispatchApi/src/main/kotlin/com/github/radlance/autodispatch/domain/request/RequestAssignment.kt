package com.github.radlance.autodispatch.domain.request

import kotlinx.serialization.Serializable

@Serializable
data class RequestAssignment(
    val driversStats: List<DriverStats>,
    val vehiclesStats: List<VehicleStats>
)
