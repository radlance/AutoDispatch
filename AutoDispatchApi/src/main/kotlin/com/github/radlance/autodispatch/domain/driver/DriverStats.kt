package com.github.radlance.autodispatch.domain.driver

import com.github.radlance.autodispatch.domain.common.Status
import kotlinx.serialization.Serializable

@Serializable
data class DriverStats(
    val driverId: Int,
    val driverName: String,
    val phoneNumber: String?,
    val driverStatus: Status,
    val userStatus: Status,
    val vehicleModel: String?,
    val vehicleLicensePlate: String?,
    val vehicleRegionCode: String?,
    val vehiclePayloadCapacity: Int?,
    val totalAssignedRequests: Long,
    val workSchedule: List<DriverWorkShift>,
    val isWorkingNow: Boolean,
    val scheduleHint: String
)
