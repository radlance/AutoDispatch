package com.github.radlance.autodispatch.request.assignment.data

import kotlinx.serialization.Serializable

@Serializable
data class DriverStatsDto(
    val driverId: Int,
    val driverName: String,
    val phoneNumber: String?,
    val driverStatus: String,
    val vehicleModel: String?,
    val vehicleLicensePlate: String?,
    val vehiclePayloadCapacity: Int?,
    val totalAssignedRequests: Long
)