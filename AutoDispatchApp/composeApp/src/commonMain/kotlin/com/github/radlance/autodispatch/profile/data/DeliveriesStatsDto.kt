package com.github.radlance.autodispatch.profile.data

import kotlinx.serialization.Serializable

@Serializable
data class DeliveriesStatsDto(
    val totalCount: Int,
    val activeCount: Int,
    val completedCount: Int,
    val canceledCount: Int,
    val onCheckCount: Int,
    val rejectedCount: Int
)