package com.github.radlance.autodispatch.request.assignment.data

import kotlinx.serialization.Serializable

@Serializable
data class DriverStatsDto(
    val driverName: String,
    val phoneNumber: String?,
    val status: String,
    val totalAssignedRequests: Long
)