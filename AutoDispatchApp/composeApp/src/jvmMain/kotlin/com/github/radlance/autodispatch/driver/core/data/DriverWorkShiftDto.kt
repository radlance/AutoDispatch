package com.github.radlance.autodispatch.driver.core.data

import kotlinx.serialization.Serializable

@Serializable
data class DriverWorkShiftDto(
    val dayOfWeek: Int,
    val startTime: String,
    val endTime: String
)
