package com.github.radlance.autodispatch.domain.request

import kotlinx.serialization.Serializable

@Serializable
data class DriverRequest(
    val id: Int,
    val requestNumber: String,
    val customer: Customer,
    val loadingPoint: Point,
    val unloadingPoint: Point,
    val cargo: Cargo,
    val createdAt: String,
    val updatedAt: String?
)
