package com.github.radlance.autodispatch.domain.request

import kotlinx.serialization.Serializable

@Serializable
data class RequestResponse(
    val cargoTypes: List<CargoType>,
    val requests: List<Request>
)
