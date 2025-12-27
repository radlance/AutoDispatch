package com.github.radlance.autodispatch.domain.statistics

import kotlinx.serialization.Serializable

@Serializable
data class TopDriverStat(
    val fullName: String,
    val avatarUrl: String?,
    val completedAssignments: Long,
    val currentStatus: String
)
