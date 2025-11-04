package com.github.radlance.autodispatch.request.assignment.data

import kotlinx.serialization.Serializable

@Serializable
data class RequestAssignmentDto(
    val driversStats: List<DriverStatsDto>,
    val vehiclesStats: List<VehicleStatsDto>
)
