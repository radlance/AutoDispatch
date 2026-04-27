package com.github.radlance.autodispatch.di

import com.github.radlance.autodispatch.auth.data.RemoteAuthRepository
import com.github.radlance.autodispatch.auth.domain.AuthRepository
import com.github.radlance.autodispatch.auth.presentation.BaseSignInValidator
import com.github.radlance.autodispatch.auth.presentation.SignInViewModel
import com.github.radlance.autodispatch.auth.presentation.SignInValidator
import com.github.radlance.autodispatch.common.data.ApiService
import com.github.radlance.autodispatch.common.data.BaseDataStoreManager
import com.github.radlance.autodispatch.common.data.BaseHandleRequest
import com.github.radlance.autodispatch.common.data.DataStoreManager
import com.github.radlance.autodispatch.common.data.HandleRequest
import com.github.radlance.autodispatch.common.data.KtorApiService
import com.github.radlance.autodispatch.core.data.LocalAppSettingsRepository
import com.github.radlance.autodispatch.core.domain.AppSettingsRepository
import com.github.radlance.autodispatch.core.presentation.AppSettingsViewModel
import com.github.radlance.autodispatch.navigation.core.DeepLinkManager
import com.github.radlance.autodispatch.navigation.core.NavigationViewModel
import com.github.radlance.autodispatch.navigation.data.LocalNavigationRepository
import com.github.radlance.autodispatch.navigation.domain.NavigationRepository
import com.github.radlance.autodispatch.profile.data.LocalProfileRepository
import com.github.radlance.autodispatch.profile.domain.ProfileRepository
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val commonModule = module {
    singleOf(::KtorApiService).bind<ApiService>()
    singleOf(::BaseHandleRequest).bind<HandleRequest>()
    singleOf(::BaseDataStoreManager).bind<DataStoreManager>()
    single { createHttpClient(dataStoreManager = get()) }
}

val navigationModule = module {
    singleOf(::LocalNavigationRepository).bind<NavigationRepository>()
    singleOf(::DeepLinkManager)
    viewModelOf(::NavigationViewModel)
}

val authModule = module {
    singleOf(::RemoteAuthRepository).bind<AuthRepository>()
    singleOf(::BaseHandleRequest).bind<HandleRequest>()
    singleOf(::BaseSignInValidator).bind<SignInValidator>()

    viewModelOf(::SignInViewModel)
}

val profileModule = module {
    singleOf(::LocalProfileRepository).bind<ProfileRepository>()
}

val settingsModule = module {
    singleOf(::LocalAppSettingsRepository).bind<AppSettingsRepository>()
    viewModelOf(::AppSettingsViewModel)
}