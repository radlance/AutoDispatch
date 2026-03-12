package com.github.radlance.autodispatch.common.utils

import com.github.radlance.autodispatch.request.core.domain.Vehicle

fun formatLicensePlate(licensePlate: String, regionCode: String?): String {
    val normalizedRegion = regionCode?.trim().orEmpty()
    return if (normalizedRegion.isBlank()) {
        licensePlate
    } else {
        "$licensePlate $normalizedRegion"
    }
}

fun Vehicle.formattedLicensePlate(): String = formatLicensePlate(licensePlate, regionCode)
