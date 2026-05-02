package com.github.radlance.autodispatch.request.assignment.data

import com.github.radlance.autodispatch.common.data.StatusDto
import com.github.radlance.autodispatch.driver.core.data.DriverWorkShiftDto
import kotlinx.serialization.Serializable

@Serializable
data class DriverStatsDto(
    val driverId: Int,
    val driverName: String,
    val phoneNumber: String?,
    val driverStatus: StatusDto,
    val userStatus: StatusDto,
    val vehicleModel: String?,
    val vehicleLicensePlate: String?,
    val vehicleRegionCode: String?,
    val vehiclePayloadCapacity: Int?,
    val totalAssignedRequests: Long,
    val workSchedule: List<DriverWorkShiftDto>,
    val isWorkingNow: Boolean,
    val scheduleHint: String
)
