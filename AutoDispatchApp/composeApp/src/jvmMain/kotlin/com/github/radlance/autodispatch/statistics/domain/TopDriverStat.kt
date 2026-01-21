package com.github.radlance.autodispatch.statistics.domain

import com.github.radlance.autodispatch.common.domain.DriverStatus

data class TopDriverStat(
    val fullName: String,
    val avatarUrl: String?,
    val completedAssignments: Long,
    val currentStatus: DriverStatus
)
