package com.github.radlance.autodispatch.auth.presentation

import com.github.radlance.autodispatch.common.presentation.Event

interface SignInEvent : Event {

    fun apply(action: SignInAction)

    class ChangeLogin(private val value: String) : SignInEvent {

        override fun apply(action: SignInAction) = action.changeLogin(value)
    }

    class ChangePassword(private val value: String) : SignInEvent {

        override fun apply(action: SignInAction) = action.changePassword(value)
    }

    class ClickSignIn(private val login: String, private val password: String) : SignInEvent {

        override fun apply(action: SignInAction) = action.signIn(login, password)
    }
}

interface SignInAction {

    fun changeLogin(value: String)

    fun changePassword(value: String)

    fun signIn(email: String, password: String)
}