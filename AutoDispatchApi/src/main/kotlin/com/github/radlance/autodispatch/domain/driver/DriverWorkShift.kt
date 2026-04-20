package com.github.radlance.autodispatch.domain.driver

import kotlinx.serialization.Serializable

@Serializable
data class DriverWorkShift(
    val dayOfWeek: Int,
    val startTime: String,
    val endTime: String
)

@Serializable
data class DriverWorkScheduleRequest(
    val shifts: List<DriverWorkShift>
)