package com.github.radlance.autodispatch.request.assignment.data

import kotlinx.serialization.Serializable

@Serializable
data class AssignRequestDto(
    val driverId: Int
)