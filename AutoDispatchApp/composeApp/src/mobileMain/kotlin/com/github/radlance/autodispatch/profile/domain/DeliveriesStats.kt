package com.github.radlance.autodispatch.profile.domain

data class DeliveriesStats(
    val activeCount: Int,
    val completedCount: Int,
    val canceledCount: Int,
    val onCheckCount: Int,
    val rejectedCount: Int
)
