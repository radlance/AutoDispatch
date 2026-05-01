package com.github.radlance.autodispatch.domain.request

import com.github.radlance.autodispatch.domain.common.Status
import kotlinx.serialization.Serializable

@Serializable
data class RequestFilters(
    val cities: List<City>,
    val cargoTypes: List<CargoType>,
    val statuses: List<Status>,
    val drivers: List<UserFilter>,
    val vehicles: List<Vehicle>
)
