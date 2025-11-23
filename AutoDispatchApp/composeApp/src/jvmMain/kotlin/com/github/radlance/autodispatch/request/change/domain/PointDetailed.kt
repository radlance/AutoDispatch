package com.github.radlance.autodispatch.request.change.domain

import kotlinx.serialization.json.JsonObject

data class PointDetailed(
    val placeId: Long,
    val lat: Double,
    val lon: Double,
    val importance: Double,
    val name: String,
    val displayName: String,
    val boundingBox: List<String>,
    val geoJson: JsonObject?
)
