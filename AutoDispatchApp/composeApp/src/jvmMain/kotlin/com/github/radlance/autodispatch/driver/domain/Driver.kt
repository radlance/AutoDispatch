package com.github.radlance.autodispatch.driver.domain

import com.github.radlance.autodispatch.common.domain.Status
import com.github.radlance.autodispatch.reuqest.core.domain.Vehicle

data class Driver(
    val fullName: String,
    val phoneNumber: String,
    val status: Status,
    val vehicle: Vehicle,
    val deliveryCount: Int
)
