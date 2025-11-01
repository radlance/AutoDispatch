package com.github.radlance.autodispatch.request.create.data

import kotlinx.serialization.Serializable

@Serializable
data class CustomerDto(
    val id: Int,
    val organizationName: String,
    val email: String,
    val phoneNumber: String?
)
