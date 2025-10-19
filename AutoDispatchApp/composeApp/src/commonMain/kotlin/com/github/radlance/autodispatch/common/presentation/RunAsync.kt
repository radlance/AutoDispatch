package com.github.radlance.autodispatch.common.presentation

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

interface RunAsync {

    fun <T : Any> async(background: suspend () -> T, ui: (T) -> Unit, scope: CoroutineScope)

    fun <T> stateInAsync(
        flow: Flow<T>,
        started: SharingStarted,
        initialValue: T,
        scope: CoroutineScope
    ): StateFlow<T>
}

internal class BaseRunAsync : RunAsync {
    override fun <T : Any> async(
        background: suspend () -> T,
        ui: (T) -> Unit,
        scope: CoroutineScope
    ) {
        scope.launch(Dispatchers.IO) {
            val result = background.invoke()

            withContext(Dispatchers.Main) {
                ui.invoke(result)
            }
        }
    }

    override fun <T> stateInAsync(
        flow: Flow<T>,
        started: SharingStarted,
        initialValue: T,
        scope: CoroutineScope
    ): StateFlow<T> = flow.stateIn(scope, started, initialValue)
}