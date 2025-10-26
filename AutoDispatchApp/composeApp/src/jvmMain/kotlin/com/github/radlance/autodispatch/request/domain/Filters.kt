package com.github.radlance.autodispatch.request.domain

data class Filters(
    val cities: List<City>,
    val cargoTypes: List<CargoType>,
    val statuses: List<RequestStatus>,
    val drivers: List<UserFilter>,
    val vehicles: List<VehicleFilter>
)
