package com.github.radlance.autodispatch.request.change.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
data class PointDto(
    @SerialName("place_id") val placeId: Long,
    val lat: String,
    val lon: String,
    val importance: Double,
    val name: String,
    @SerialName("display_name") val displayName: String,
    @SerialName("boundingbox") val boundingBox: List<String>,
    @SerialName("geojson") val geoJson: JsonObject?
)
