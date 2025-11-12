package com.github.radlance.autodispatch.navigation.core

import com.github.radlance.autodispatch.common.presentation.BaseViewModel
import com.github.radlance.autodispatch.navigation.domain.NavigationRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOn

class NavigationViewModel(
    navigationRepository: NavigationRepository
) : BaseViewModel() {

    val authorizedState: StateFlow<Boolean> =
        navigationRepository.authorized.flowOn(Dispatchers.IO).stateInViewModel(
        initialValue = false
    )

    val sessionExpired: StateFlow<Boolean> =
        navigationRepository.sessionExpired.flowOn(Dispatchers.IO).stateInViewModel(
        initialValue = false
    )
}