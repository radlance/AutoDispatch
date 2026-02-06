package com.github.radlance.autodispatch.request.change.domain

import com.github.radlance.autodispatch.request.core.domain.Point

data class ValidatedPoint(
    val point: Point,
    val cityName: String
)
