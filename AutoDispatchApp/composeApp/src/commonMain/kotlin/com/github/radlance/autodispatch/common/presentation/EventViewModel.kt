package com.github.radlance.autodispatch.common.presentation

interface EventViewModel<T : Event> {

    fun reduce(event: T)
}

interface Event