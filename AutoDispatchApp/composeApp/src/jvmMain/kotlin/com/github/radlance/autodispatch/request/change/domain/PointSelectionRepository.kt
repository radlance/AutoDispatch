package com.github.radlance.autodispatch.request.change.domain

import com.github.radlance.autodispatch.common.domain.FetchResult
import com.github.radlance.autodispatch.request.core.domain.Point

interface PointSelectionRepository {

    suspend fun fetchCoords(): FetchResult<Coords, String>

    suspend fun searchPoint(query: String): List<PointDetailed>

    suspend fun validatePointInCity(
        point: Point,
        selectedCityName: String
    ): FetchResult<ValidatedPoint, PointValidationError>
}