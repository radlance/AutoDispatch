package com.github.radlance.autodispatch.navigation.core

import androidx.lifecycle.viewModelScope
import com.github.radlance.autodispatch.common.presentation.BaseViewModel
import com.github.radlance.autodispatch.navigation.domain.NavigationRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class NavigationViewModel(
    private val navigationRepository: NavigationRepository
) : BaseViewModel() {

    val authorizedState: Boolean = runBlocking {
        navigationRepository.authorized.first()
    }

    val sessionExpired: StateFlow<Boolean> =
        navigationRepository.sessionExpired.flowOn(Dispatchers.IO).stateInViewModel(
        initialValue = false
    )

    fun updateExpirationState() {
        viewModelScope.launch(Dispatchers.IO) {
            navigationRepository.saveSessionExpired(expired = false)
            launch {
                navigationRepository.deleteToken()
            }
        }
    }
}