package com.github.radlance.autodispatch.profile.presentation

import com.github.radlance.autodispatch.common.presentation.BaseViewModel
import com.github.radlance.autodispatch.common.presentation.FetchResultUiState
import com.github.radlance.autodispatch.common.presentation.toUiState
import com.github.radlance.autodispatch.profile.domain.DriverProfileRepository
import com.github.radlance.autodispatch.profile.domain.ProfileDetails
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.onStart

class ProfileViewModel(private val repository: DriverProfileRepository) : BaseViewModel() {

    private val profileStateMutable =
        MutableStateFlow<FetchResultUiState<ProfileDetails, String>>(FetchResultUiState.Idle)
    val profileState = profileStateMutable.onStart {
        loadProfile()
    }.stateInViewModel(initialValue = profileStateMutable.value)

    fun loadProfile() {
        profileStateMutable.value = FetchResultUiState.Loading
        handle(background = repository::profileDetails) {
            profileStateMutable.value = it.toUiState()
        }
    }
}