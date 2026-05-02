package com.github.radlance.autodispatch.request.assignment.domain

import com.github.radlance.autodispatch.admin.core.domain.UserStatus
import com.github.radlance.autodispatch.common.domain.DriverStatus
import com.github.radlance.autodispatch.driver.core.domain.DriverWorkShift

data class DriverStats(
    val driverId: Int,
    val driverName: String,
    val phoneNumber: String?,
    val driverStatus: DriverStatus,
    val userStatus: UserStatus,
    val vehicleModel: String?,
    val vehicleLicensePlate: String?,
    val vehicleRegionCode: String?,
    val vehiclePayloadCapacity: Int?,
    val totalAssignedRequests: Long,
    val workSchedule: List<DriverWorkShift>,
    val isWorkingNow: Boolean,
    val scheduleHint: String
)
