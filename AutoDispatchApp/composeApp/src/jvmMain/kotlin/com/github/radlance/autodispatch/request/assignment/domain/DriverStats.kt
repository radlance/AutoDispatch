package com.github.radlance.autodispatch.request.assignment.domain

import com.github.radlance.autodispatch.common.domain.DriverStatus

data class DriverStats(
    val driverId: Int,
    val driverName: String,
    val phoneNumber: String?,
    val driverStatus: DriverStatus,
    val vehicleModel: String?,
    val vehicleLicensePlate: String?,
    val vehicleRegionCode: String?,
    val vehiclePayloadCapacity: Int?,
    val totalAssignedRequests: Long
)
