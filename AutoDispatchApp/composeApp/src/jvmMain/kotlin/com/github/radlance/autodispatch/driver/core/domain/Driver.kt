package com.github.radlance.autodispatch.driver.core.domain

import com.github.radlance.autodispatch.admin.core.domain.UserStatus
import com.github.radlance.autodispatch.common.domain.DriverStatus
import com.github.radlance.autodispatch.profile.domain.DeliveriesStats
import com.github.radlance.autodispatch.request.core.domain.Vehicle
import kotlinx.serialization.Serializable

@Serializable
data class Driver(
    val id: Int,
    val fullName: String,
    val avatarUrl: String?,
    val phoneNumber: String,
    val status: DriverStatus,
    val userStatus: UserStatus,
    val vehicle: Vehicle?,
    val deliveriesStats: DeliveriesStats,
    val workSchedule: List<DriverWorkShift>
)
