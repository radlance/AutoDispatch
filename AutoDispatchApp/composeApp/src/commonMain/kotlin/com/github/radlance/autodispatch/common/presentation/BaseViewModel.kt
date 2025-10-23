package com.github.radlance.autodispatch.common.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

abstract class BaseViewModel : ViewModel() {

    protected fun <T : Any> handle(background: suspend () -> T, ui: (T) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = background.invoke()

            withContext(Dispatchers.Main) {
                ui.invoke(result)
            }
        }
    }

    protected fun <T> Flow<T>.stateInViewModel(initialValue: T): StateFlow<T> {
        return stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
            initialValue
        )
    }

}