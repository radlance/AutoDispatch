package com.github.radlance.autodispatch.request.data

import kotlinx.serialization.Serializable

@Serializable
data class RequestResponseDto(
    val cargoTypes: List<CargoTypeDto>,
    val requests: List<RequestDto>
)
