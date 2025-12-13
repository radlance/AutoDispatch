package com.github.radlance.autodispatch.driver.core.domain

import com.github.radlance.autodispatch.common.domain.Status
import com.github.radlance.autodispatch.reuqest.core.domain.Vehicle
import kotlinx.serialization.Serializable

@Serializable
data class Driver(
    val id: Int,
    val fullName: String,
    val phoneNumber: String,
    val status: Status,
    val vehicle: Vehicle?,
    val deliveryCount: Int
)
