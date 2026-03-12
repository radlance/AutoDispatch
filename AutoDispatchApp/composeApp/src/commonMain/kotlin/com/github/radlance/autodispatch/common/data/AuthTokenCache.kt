package com.github.radlance.autodispatch.common.data

import io.ktor.client.HttpClient
import io.ktor.client.plugins.auth.providers.BearerAuthProvider
import io.ktor.client.plugins.auth.authProviders

fun HttpClient.clearBearerTokenCache() {
    authProviders
        .filterIsInstance<BearerAuthProvider>()
        .forEach { it.clearToken() }
}
