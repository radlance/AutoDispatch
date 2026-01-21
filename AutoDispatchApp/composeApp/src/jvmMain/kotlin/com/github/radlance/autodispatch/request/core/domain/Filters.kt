package com.github.radlance.autodispatch.request.core.domain

import com.github.radlance.autodispatch.common.domain.RequestStatus

data class Filters(
    val cities: List<City>,
    val cargoTypes: List<CargoType>,
    val statuses: List<RequestStatus>,
    val drivers: List<UserFilter>,
    val vehicles: List<Vehicle>
)
