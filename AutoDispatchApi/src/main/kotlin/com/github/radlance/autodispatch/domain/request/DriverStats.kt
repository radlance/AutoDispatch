package com.github.radlance.autodispatch.domain.request

import kotlinx.serialization.Serializable

@Serializable
data class DriverStats(
    val driverName: String,
    val phoneNumber: String?,
    val status: String,
    val totalAssignedRequests: Long
)