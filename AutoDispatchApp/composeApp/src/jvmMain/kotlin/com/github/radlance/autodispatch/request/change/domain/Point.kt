package com.github.radlance.autodispatch.request.change.domain

import kotlinx.serialization.json.JsonObject

data class  Point(
    val placeId: Long,
    val lat: String,
    val lon: String,
    val importance: Double,
    val name: String,
    val displayName: String,
    val boundingBox: List<String>,
    val geoJson: JsonObject?
)
