package com.github.radlance.autodispatch.request.core.domain

import com.github.radlance.autodispatch.common.domain.Status

data class Filters(
    val cities: List<City>,
    val cargoTypes: List<CargoType>,
    val statuses: List<Status>,
    val drivers: List<UserFilter>,
    val vehicles: List<Vehicle>
)
