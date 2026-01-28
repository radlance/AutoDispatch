package com.github.radlance.autodispatch.domain.request

import kotlinx.serialization.Serializable

@Serializable
data class RequestEmailView(
    val requestId: Int,
    val requestNumber: String,
    val customerEmail: String?,
    val cargoTypeName: String?,
    val transportationDescription: String?,
    val cargoDescription: String?,
    val loadingAddress: String?,
    val unloadingAddress: String?
)
