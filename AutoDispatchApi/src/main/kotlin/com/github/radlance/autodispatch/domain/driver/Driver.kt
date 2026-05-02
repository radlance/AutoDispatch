package com.github.radlance.autodispatch.domain.driver

import com.github.radlance.autodispatch.domain.common.Status
import com.github.radlance.autodispatch.domain.profile.DeliveriesStats
import com.github.radlance.autodispatch.domain.request.Vehicle
import kotlinx.serialization.Serializable

@Serializable
data class Driver(
    val id: Int,
    val fullName: String,
    val avatarUrl: String?,
    val phoneNumber: String,
    val status: Status,
    val userStatus: Status,
    val vehicle: Vehicle?,
    val deliveriesStats: DeliveriesStats,
    val workSchedule: List<DriverWorkShift>
)
