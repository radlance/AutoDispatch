package com.github.radlance.autodispatch.navigation.core

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class DeepLinkManager {
    private val _pendingRoute = MutableStateFlow<Int?>(null)
    val pendingRoute = _pendingRoute.asStateFlow()

    fun handleRawUrl(url: String) {
        val id = url.substringAfterLast("/").toIntOrNull()
        if (id != null) {
            _pendingRoute.update { id }
        }
    }

    fun consume() {
        _pendingRoute.update { null }
    }
}