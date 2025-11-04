package com.github.radlance.autodispatch.request.assignment.domain

data class RequestAssignment(
    val driversStats: List<DriverStats>,
    val vehiclesStats: List<VehicleStats>
)
