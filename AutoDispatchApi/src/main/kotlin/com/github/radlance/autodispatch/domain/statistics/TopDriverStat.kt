package com.github.radlance.autodispatch.domain.statistics

import com.github.radlance.autodispatch.domain.common.Status
import kotlinx.serialization.Serializable

@Serializable
data class TopDriverStat(
    val fullName: String,
    val avatarUrl: String?,
    val completedAssignments: Long,
    val currentStatus: Status
)
