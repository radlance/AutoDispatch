package com.github.radlance.autodispatch.request.change.data

import com.github.radlance.autodispatch.common.data.ApiServiceJvm
import com.github.radlance.autodispatch.common.data.HandleRequest
import com.github.radlance.autodispatch.common.data.toCoords
import com.github.radlance.autodispatch.common.data.toPointDetailed
import com.github.radlance.autodispatch.common.domain.FetchResult
import com.github.radlance.autodispatch.request.change.domain.Coords
import com.github.radlance.autodispatch.request.change.domain.PointDetailed
import com.github.radlance.autodispatch.request.change.domain.PointSelectionRepository

class RemotePointSelectionRepository(
    private val apiService: ApiServiceJvm,
    private val handleRequest: HandleRequest
) : PointSelectionRepository {
    override suspend fun fetchCoords(): FetchResult<Coords, String> =
        handleRequest.handle {
            apiService.coords().toCoords()
        }

    override suspend fun searchPoint(query: String): List<PointDetailed> {
        return try {
            apiService.points(query).sortedByDescending { it.importance }.map { it.toPointDetailed() }
        } catch (_: Exception) {
            emptyList()
        }
    }
}