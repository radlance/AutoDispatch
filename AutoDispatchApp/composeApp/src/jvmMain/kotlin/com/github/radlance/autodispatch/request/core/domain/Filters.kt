package com.github.radlance.autodispatch.request.core.domain

import com.github.radlance.autodispatch.reuqest.core.domain.CargoType
import com.github.radlance.autodispatch.common.domain.Status
import com.github.radlance.autodispatch.reuqest.core.domain.Vehicle

data class Filters(
    val cities: List<City>,
    val cargoTypes: List<CargoType>,
    val statuses: List<Status>,
    val drivers: List<UserFilter>,
    val vehicles: List<Vehicle>
)
