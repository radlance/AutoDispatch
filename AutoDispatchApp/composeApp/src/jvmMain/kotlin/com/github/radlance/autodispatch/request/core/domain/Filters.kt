package com.github.radlance.autodispatch.request.core.domain

import com.github.radlance.autodispatch.reuqest.core.domain.CargoType
import com.github.radlance.autodispatch.reuqest.core.domain.RequestStatus
import com.github.radlance.autodispatch.reuqest.core.domain.VehicleFilter

data class Filters(
    val cities: List<City>,
    val cargoTypes: List<CargoType>,
    val statuses: List<RequestStatus>,
    val drivers: List<UserFilter>,
    val vehicles: List<VehicleFilter>
)
