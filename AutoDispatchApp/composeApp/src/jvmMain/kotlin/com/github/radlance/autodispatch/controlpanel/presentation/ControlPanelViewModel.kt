package com.github.radlance.autodispatch.controlpanel.presentation

import com.github.radlance.autodispatch.common.presentation.BaseViewModel
import com.github.radlance.autodispatch.common.presentation.FetchResultUiState
import com.github.radlance.autodispatch.common.presentation.toUiState
import com.github.radlance.autodispatch.profile.domain.ProfileRepository
import com.github.radlance.autodispatch.profile.domain.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart

class ControlPanelViewModel(
    private val profileRepository: ProfileRepository
) : BaseViewModel() {
    private val loadProfileUiStateMutable =
        MutableStateFlow<FetchResultUiState<User, String>>(FetchResultUiState.Idle)

    val loadProfileUiState: StateFlow<FetchResultUiState<User, String>> =
        loadProfileUiStateMutable.onStart {
            loadProfile()
        }.stateInViewModel(initialValue = loadProfileUiStateMutable.value)

    fun loadProfile() {
        loadProfileUiStateMutable.value = FetchResultUiState.Loading
        handle(background = profileRepository::profile) { result ->
            loadProfileUiStateMutable.value = result.toUiState()
        }
    }
}