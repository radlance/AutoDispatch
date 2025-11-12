package com.github.radlance.autodispatch

import androidx.compose.ui.window.ComposeUIViewController
import com.github.radlance.autodispatch.core.App
import com.github.radlance.autodispatch.di.authModule
import com.github.radlance.autodispatch.di.commonModule
import com.github.radlance.autodispatch.di.dataStoreModule
import com.github.radlance.autodispatch.di.deliveryModule
import com.github.radlance.autodispatch.di.navigationModule
import com.github.radlance.autodispatch.di.profileModule
import com.github.radlance.autodispatch.di.requestModule
import org.koin.core.context.startKoin

fun MainViewController() = ComposeUIViewController(
    configure = {
        startKoin {
            modules(
                dataStoreModule,
                commonModule,
                navigationModule,
                authModule,
                profileModule,
                requestModule,
                deliveryModule
            )
        }
    }
) {
    App()
}