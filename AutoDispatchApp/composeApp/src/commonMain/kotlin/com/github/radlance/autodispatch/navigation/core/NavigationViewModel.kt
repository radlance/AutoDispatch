package com.github.radlance.autodispatch.navigation.core

import com.github.radlance.autodispatch.common.presentation.BaseViewModel
import com.github.radlance.autodispatch.navigation.domain.NavigationRepository
import kotlinx.coroutines.flow.StateFlow

class NavigationViewModel(
    navigationRepository: NavigationRepository
) : BaseViewModel() {

    val authorizedState: StateFlow<Boolean> = navigationRepository.authorized.stateInViewModel(
        initialValue = false
    )

    val sessionExpired: StateFlow<Boolean> = navigationRepository.sessionExpired.stateInViewModel(
        initialValue = false
    )
}