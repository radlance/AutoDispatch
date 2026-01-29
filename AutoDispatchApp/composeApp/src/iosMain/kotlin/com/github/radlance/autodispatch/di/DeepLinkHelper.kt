package com.github.radlance.autodispatch.di

import com.github.radlance.autodispatch.navigation.core.DeepLinkManager
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class DeepLinkHelper : KoinComponent {
    private val deepLinkManager: DeepLinkManager by inject()

    fun handleDeepLink(url: String) {
        deepLinkManager.handleRawUrl(url)
    }
}