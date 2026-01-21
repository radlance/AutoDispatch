package com.github.radlance.autodispatch.request.assignment.data

import com.github.radlance.autodispatch.common.data.StatusDto
import kotlinx.serialization.Serializable

@Serializable
data class DriverStatsDto(
    val driverId: Int,
    val driverName: String,
    val phoneNumber: String?,
    val driverStatus: StatusDto,
    val vehicleModel: String?,
    val vehicleLicensePlate: String?,
    val vehiclePayloadCapacity: Int?,
    val totalAssignedRequests: Long
)