package com.github.radlance.autodispatch.exception

data class MissingCredentialException(override val message: String = "Missing credentials") : RuntimeException()