package com.github.radlance.autodispatch.request.assignment.domain

data class DriverStats(
    val driverName: String,
    val phoneNumber: String?,
    val status: String,
    val totalAssignedRequests: Long
)