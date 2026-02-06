package com.github.radlance.autodispatch.request.change.data

import com.github.radlance.autodispatch.common.data.ApiServiceJvm
import com.github.radlance.autodispatch.common.data.HandleRequest
import com.github.radlance.autodispatch.common.data.belongsTo
import com.github.radlance.autodispatch.common.data.toCoords
import com.github.radlance.autodispatch.common.data.toPointDetailed
import com.github.radlance.autodispatch.common.domain.FetchResult
import com.github.radlance.autodispatch.request.change.domain.Coords
import com.github.radlance.autodispatch.request.change.domain.PointDetailed
import com.github.radlance.autodispatch.request.change.domain.PointSelectionRepository
import com.github.radlance.autodispatch.request.change.domain.PointValidationError
import com.github.radlance.autodispatch.request.change.domain.ValidatedPoint
import com.github.radlance.autodispatch.request.core.domain.Point

class RemotePointSelectionRepository(
    private val apiService: ApiServiceJvm,
    private val handleRequest: HandleRequest
) : PointSelectionRepository {
    override suspend fun cityCenter(cityName: String): FetchResult<Coords, String> =
        handleRequest.handle {
            apiService.points(cityName).first().toCoords()
        }

    override suspend fun searchPoint(query: String): List<PointDetailed> {
        return try {
            apiService.points(query).sortedByDescending { it.importance }.map { it.toPointDetailed() }
        } catch (_: Exception) {
            emptyList()
        }
    }

    override suspend fun validatePointInCity(
        point: Point,
        selectedCityName: String
    ): FetchResult<ValidatedPoint, PointValidationError> {

        val reverse = try {
            apiService.reverse(point.lat, point.lon)
        } catch (_: Exception) {
            return FetchResult.Error(PointValidationError.Network)
        }

        val address = reverse.address
            ?: return FetchResult.Error(PointValidationError.CityNotResolved)

        if (!address.belongsTo(selectedCityName)) {
            return FetchResult.Error(
                PointValidationError.PointOutsideCity(
                    expectedCity = selectedCityName
                )
            )
        }

        return FetchResult.Success(
            ValidatedPoint(
                point = point,
                cityName = selectedCityName
            )
        )
    }

}