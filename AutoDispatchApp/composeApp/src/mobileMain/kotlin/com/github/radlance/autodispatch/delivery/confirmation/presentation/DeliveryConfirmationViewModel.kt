package com.github.radlance.autodispatch.delivery.confirmation.presentation

import androidx.compose.runtime.mutableStateListOf
import com.github.radlance.autodispatch.common.presentation.BaseViewModel

class DeliveryConfirmationViewModel : BaseViewModel() {
    val documents = mutableStateListOf<ByteArray>()
}