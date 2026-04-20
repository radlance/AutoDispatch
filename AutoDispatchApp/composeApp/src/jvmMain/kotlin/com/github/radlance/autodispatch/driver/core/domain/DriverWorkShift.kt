package com.github.radlance.autodispatch.driver.core.domain

import kotlinx.serialization.Serializable

@Serializable
data class DriverWorkShift(
    val dayOfWeek: Int,
    val startTime: String,
    val endTime: String
)
