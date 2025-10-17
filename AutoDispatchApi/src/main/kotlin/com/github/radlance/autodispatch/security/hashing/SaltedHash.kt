package com.github.radlance.autodispatch.security.hashing

data class SaltedHash(
    val hash: String,
    val salt: String
)