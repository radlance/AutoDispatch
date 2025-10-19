package com.github.radlance.autodispatch.navigation.core

import com.github.radlance.autodispatch.common.presentation.BaseViewModel
import com.github.radlance.autodispatch.common.presentation.RunAsync
import com.github.radlance.autodispatch.navigation.domain.NavigationRepository
import kotlinx.coroutines.flow.StateFlow

class NavigationViewModel(
    runAsync: RunAsync,
    navigationRepository: NavigationRepository
) : BaseViewModel(runAsync) {

    val authorizedState: StateFlow<Boolean> = navigationRepository.authorized.stateInViewModel(
        initialValue = false
    )

    val sessionExpired: StateFlow<Boolean> = navigationRepository.sessionExpired.stateInViewModel(
        initialValue = false
    )
}