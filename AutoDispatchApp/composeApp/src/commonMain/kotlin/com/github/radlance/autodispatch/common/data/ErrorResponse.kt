package com.github.radlance.autodispatch.common.data

import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponse(
    val message: String,
    val errorCode: String
)