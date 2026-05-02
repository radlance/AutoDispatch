package com.github.radlance.autodispatch.admin.change.preentation

import com.github.radlance.autodispatch.admin.change.domain.ChangeUserRepository
import com.github.radlance.autodispatch.common.presentation.BaseViewModel
import com.github.radlance.autodispatch.common.presentation.FetchResultUiState
import com.github.radlance.autodispatch.common.presentation.toUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class ChangeUserViewModel(
    private val repository: ChangeUserRepository
) : BaseViewModel() {

    private val blockUserStateMutable = MutableStateFlow<FetchResultUiState<Unit, String>>(
        FetchResultUiState.Idle
    )
    val blockUserState = blockUserStateMutable.asStateFlow()

    fun blockUser(userId: Int) {
        blockUserStateMutable.value = FetchResultUiState.Loading

        handle(
            background = { repository.blockUser(userId) }
        ) {
            blockUserStateMutable.value = it.toUiState()
        }
    }

    fun unblockUser(userId: Int) {
        blockUserStateMutable.value = FetchResultUiState.Loading

        handle(
            background = { repository.unblockUser(userId) }
        ) {
            blockUserStateMutable.value = it.toUiState()
        }
    }

    fun deleteUser(userId: Int) {
        blockUserStateMutable.value = FetchResultUiState.Loading

        handle(
            background = { repository.deleteUser(userId) }
        ) {
            blockUserStateMutable.value = it.toUiState()
        }
    }

    fun resetBlockState() {
        blockUserStateMutable.value = FetchResultUiState.Idle
    }
}