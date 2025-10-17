package com.github.radlance.autodispatch.exception

data class NoAccessException(override val message: String? = null) : RuntimeException()