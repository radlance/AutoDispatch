package com.github.radlance.autodispatch.navigation.core

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class DeepLinkManager {
    private val _pendingRoute = MutableStateFlow<PendingRoute?>(null)
    val pendingRoute = _pendingRoute.asStateFlow()

    fun handleRawUrl(url: String) {
        val parts = url.split("?", limit = 2)
        val path = parts.getOrNull(0) ?: ""
        val query = parts.getOrNull(1) ?: ""

        val id = path.substringAfterLast("/").toIntOrNull()

        val number = query.split("&")
            .map { it.split("=") }
            .firstOrNull { it.size == 2 && it[0] == "number" }
            ?.get(1)

        if (id != null && number != null) {
            _pendingRoute.update { PendingRoute(id, number) }
        }
    }

    fun consume() {
        _pendingRoute.update { null }
    }
}