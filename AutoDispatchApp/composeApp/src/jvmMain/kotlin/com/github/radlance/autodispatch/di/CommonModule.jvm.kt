package com.github.radlance.autodispatch.di

import com.github.radlance.autodispatch.controlpanel.presentation.ControlPanelViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val controlPanelModule
    get() = module {
        viewModelOf(::ControlPanelViewModel)
    }