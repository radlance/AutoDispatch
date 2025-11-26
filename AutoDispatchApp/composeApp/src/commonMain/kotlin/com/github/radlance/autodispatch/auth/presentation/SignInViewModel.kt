package com.github.radlance.autodispatch.auth.presentation

import com.github.radlance.autodispatch.auth.domain.AuthRepository
import com.github.radlance.autodispatch.auth.domain.LoginResponse
import com.github.radlance.autodispatch.common.presentation.BaseViewModel
import com.github.radlance.autodispatch.common.presentation.EventViewModel
import com.github.radlance.autodispatch.common.presentation.FetchResultUiState
import com.github.radlance.autodispatch.common.presentation.toUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SignInViewModel(
    private val signInValidator: SignInValidator,
    private val signInRepository: AuthRepository,
) : BaseViewModel(), EventViewModel<SignInEvent> {

    private val fieldsUiStateMutable = MutableStateFlow(SignInFieldsUiState())

    val fieldsUiState get() = fieldsUiStateMutable.asStateFlow()

    private val authResultUiStateMutable =
        MutableStateFlow<FetchResultUiState<LoginResponse, String>>(FetchResultUiState.Idle)

    val authResultUiState get() = authResultUiStateMutable.asStateFlow()

    override fun reduce(event: SignInEvent) {
        val action = object : SignInAction {
            override fun changeLogin(value: String) {
                fieldsUiStateMutable.update { state ->
                    state.copy(
                        loginFieldValue = value,
                        loginErrorMessage = ""
                    )
                }
            }

            override fun changePassword(value: String) {
                fieldsUiStateMutable.update { state ->
                    state.copy(
                        passwordFieldValue = value,
                        passwordErrorMessage = ""
                    )
                }
            }

            override fun signIn(email: String, password: String) {
                fieldsUiStateMutable.update {
                    with(signInValidator) {
                        it.copy(
                            loginErrorMessage = validationLoginMessage(email),
                            passwordErrorMessage = validationPasswordMessage(password)
                        )
                    }
                }

                with(fieldsUiState.value) {
                    if (loginErrorMessage.isEmpty() && passwordErrorMessage.isEmpty()) {
                        authResultUiStateMutable.value = FetchResultUiState.Loading

                        handle(background = {
                            signInRepository.signIn(
                                email,
                                password
                            )
                        }) { result ->
                            authResultUiStateMutable.value = result.toUiState()
                        }
                    }
                }
            }

            override fun resetState() {
                authResultUiStateMutable.value = FetchResultUiState.Idle
            }
        }
        event.apply(action = action)
    }
}