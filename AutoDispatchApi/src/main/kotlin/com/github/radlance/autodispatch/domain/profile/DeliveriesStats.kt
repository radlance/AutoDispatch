package com.github.radlance.autodispatch.domain.profile

import kotlinx.serialization.Serializable

@Serializable
data class DeliveriesStats(
    val activeCount: Int,
    val completedCount: Int,
    val canceledCount: Int,
    val onCheckCount: Int,
    val rejectedCount: Int
)
