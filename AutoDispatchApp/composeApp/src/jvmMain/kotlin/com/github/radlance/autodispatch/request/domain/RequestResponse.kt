package com.github.radlance.autodispatch.request.domain

data class RequestResponse(
    val cargoTypes: List<CargoType>,
    val requests: List<Request>
)
