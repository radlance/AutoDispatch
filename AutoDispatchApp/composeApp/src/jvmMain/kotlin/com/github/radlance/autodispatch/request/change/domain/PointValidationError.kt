package com.github.radlance.autodispatch.request.change.domain

sealed interface PointValidationError {

    data object Network : PointValidationError

    data object CityNotResolved : PointValidationError

    data class PointOutsideCity(
        val expectedCity: String
    ) : PointValidationError
}
