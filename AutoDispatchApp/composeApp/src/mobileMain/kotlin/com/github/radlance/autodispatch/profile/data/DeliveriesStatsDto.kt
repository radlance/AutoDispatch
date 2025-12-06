package com.github.radlance.autodispatch.profile.data

import kotlinx.serialization.Serializable

@Serializable
data class DeliveriesStatsDto(
    val activeCount: Int,
    val completedCount: Int,
    val canceledCount: Int,
    val onCheckCount: Int,
    val rejectedCount: Int
)
