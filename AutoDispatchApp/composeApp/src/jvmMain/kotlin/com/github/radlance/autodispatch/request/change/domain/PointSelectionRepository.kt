package com.github.radlance.autodispatch.request.change.domain

import com.github.radlance.autodispatch.common.domain.FetchResult

interface PointSelectionRepository {

    suspend fun fetchCoords(): FetchResult<Coords, String>

    suspend fun searchPoint(query: String): List<Point>
}