package com.github.radlance.autodispatch

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform