package com.github.radlance.autodispatch.profile.domain

import kotlinx.serialization.Serializable

@Serializable
data class DeliveriesStats(
    val totalCount: Int,
    val activeCount: Int,
    val completedCount: Int,
    val canceledCount: Int,
    val onCheckCount: Int,
    val rejectedCount: Int
)