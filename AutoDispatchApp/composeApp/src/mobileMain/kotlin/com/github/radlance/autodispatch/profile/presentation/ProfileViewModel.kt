package com.github.radlance.autodispatch.profile.presentation

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.github.radlance.autodispatch.common.presentation.BaseViewModel
import com.github.radlance.autodispatch.common.presentation.FetchResultUiState
import com.github.radlance.autodispatch.common.presentation.toUiState
import com.github.radlance.autodispatch.profile.domain.DriverProfileRepository
import com.github.radlance.autodispatch.profile.domain.ProfileDetails
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class ProfileViewModel(private val repository: DriverProfileRepository) : BaseViewModel() {

    private val _avatar = mutableStateOf<ByteArray?>(null)
    val avatar: State<ByteArray?> = _avatar
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

    fun uploadProfileImage(newImage: ByteArray) {
        val previousAvatar = _avatar.value

        _avatar.value = newImage

        handle(background = { repository.uploadProfileImage(newImage) }) { result ->
            val uiState = result.toUiState()

            if (uiState is FetchResultUiState.Error) {
                _avatar.value = previousAvatar
            }
        }
    }

    fun deleteProfileImage() {
        val previousAvatar = _avatar.value
        _avatar.value = null

        handle(background = { repository.deleteProfileImage() }) { result ->
            val uiState = result.toUiState()
            if (uiState is FetchResultUiState.Error) {
                _avatar.value = previousAvatar
            }
        }
    }


    fun logout() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.logout()
        }
    }

    fun clearTemporaryAvatar() {
        _avatar.value = null
    }
}