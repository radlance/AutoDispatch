package com.github.radlance.autodispatch.driver.request.domain

import com.github.radlance.autodispatch.request.core.domain.Cargo
import com.github.radlance.autodispatch.request.core.domain.Customer
import com.github.radlance.autodispatch.request.core.domain.Point
import kotlinx.datetime.LocalDateTime

data class DriverRequest(
    val id: Int,
    val requestNumber: String,
    val customer: Customer,
    val loadingPoint: Point,
    val unloadingPoint: Point,
    val cargo: Cargo,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime?
)
