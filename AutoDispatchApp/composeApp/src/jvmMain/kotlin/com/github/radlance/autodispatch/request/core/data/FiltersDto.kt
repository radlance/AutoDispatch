package com.github.radlance.autodispatch.request.core.data

import com.github.radlance.autodispatch.reuqest.core.data.CargoTypeDto
import com.github.radlance.autodispatch.reuqest.core.data.RequestStatusDto
import com.github.radlance.autodispatch.reuqest.core.data.VehicleFilterDto
import kotlinx.serialization.Serializable

@Serializable
data class FiltersDto(
    val cities: List<CityDto>,
    val cargoTypes: List<CargoTypeDto>,
    val statuses: List<RequestStatusDto>,
    val drivers: List<UserFilterDto>,
    val vehicles: List<VehicleFilterDto>
)
