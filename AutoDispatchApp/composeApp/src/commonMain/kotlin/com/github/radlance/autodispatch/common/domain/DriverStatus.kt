package com.github.radlance.autodispatch.common.domain

enum class DriverStatus(
    val id: Int,
    val title: String
) {
    Free(1, "Свободен"),
    OnRoute(2, "В рейсе");

    companion object {
        fun fromId(id: Int): DriverStatus? =
            entries.firstOrNull { it.id == id }
    }
}

fun Int.toDriverStatus(): DriverStatus =
    DriverStatus.fromId(this) ?: error("Unknown driver status id=$this")