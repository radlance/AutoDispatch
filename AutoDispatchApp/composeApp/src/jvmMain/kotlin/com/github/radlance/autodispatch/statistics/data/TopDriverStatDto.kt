package com.github.radlance.autodispatch.statistics.data

import com.github.radlance.autodispatch.common.data.StatusDto
import kotlinx.serialization.Serializable

@Serializable
data class TopDriverStatDto(
    val fullName: String,
    val avatarUrl: String?,
    val completedAssignments: Long,
    val currentStatus: StatusDto
)
