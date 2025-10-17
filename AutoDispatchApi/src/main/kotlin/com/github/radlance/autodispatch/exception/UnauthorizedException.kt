package com.github.radlance.autodispatch.exception

data class UnauthorizedException(override val message: String? = null) : RuntimeException()