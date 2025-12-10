package com.github.radlance.autodispatch.common.domain

import kotlinx.serialization.Serializable

@Serializable
data class Status(
    val id: Int,
    val name: String
)