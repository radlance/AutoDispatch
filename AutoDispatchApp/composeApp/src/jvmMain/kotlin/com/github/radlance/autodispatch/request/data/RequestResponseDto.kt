package com.github.radlance.autodispatch.request.data

import kotlinx.serialization.Serializable

@Serializable
data class RequestResponseDto(
    val departureCities: List<String>,
    val destinationCities: List<String>,
    val cargoTypes: List<String>,
    val statuses: List<String>,
    val drivers: List<String>,
    val vehicles: List<String>,
    val requests: List<RequestDto>
)
