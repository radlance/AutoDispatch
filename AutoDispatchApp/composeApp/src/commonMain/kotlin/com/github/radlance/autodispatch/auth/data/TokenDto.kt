package com.github.radlance.autodispatch.auth.data

import kotlinx.serialization.Serializable

@Serializable
internal data class TokenDto(val accessToken: String)