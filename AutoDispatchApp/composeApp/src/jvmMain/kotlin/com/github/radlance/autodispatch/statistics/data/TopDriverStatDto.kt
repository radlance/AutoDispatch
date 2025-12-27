package com.github.radlance.autodispatch.statistics.data

import kotlinx.serialization.Serializable

@Serializable
data class TopDriverStatDto(
    val fullName: String,
    val completedAssignments: Long,
    val currentStatus: String
)
