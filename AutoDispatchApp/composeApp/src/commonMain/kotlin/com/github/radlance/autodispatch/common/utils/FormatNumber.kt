package com.github.radlance.autodispatch.common.utils

import com.github.radlance.autodispatch.request.core.domain.Point

fun Double.formatNumberNoTrailingZeros(): String {
    return if (this % 1.0 == 0.0) {
        this.toInt().toString()
    } else {
        var s = this.toString()
        if (s.contains('.')) {
            s = s.trimEnd('0').trimEnd('.')
        }
        s
    }
}

fun Double?.formatKg(): String =
    this?.let { "${it.formatNumberNoTrailingZeros()} кг" } ?: "—"

fun Double?.formatM3(): String =
    this?.let { "${it.formatNumberNoTrailingZeros()} м\u00B3" } ?: "—"

fun Point.toStringAddress(): String {
    return address ?: "$lat, $lon"
}