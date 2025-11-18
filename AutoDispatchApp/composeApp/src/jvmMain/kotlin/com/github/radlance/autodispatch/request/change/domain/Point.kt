package com.github.radlance.autodispatch.request.change.domain

data class Point(
    val placeId: Long,
    val lat: String,
    val lon: String,
    val importance: Double,
    val name: String,
    val displayName: String,
    val boundingBox: List<String>
)
