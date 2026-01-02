package com.github.radlance.autodispatch.delivery.core.data

import com.github.radlance.autodispatch.delivery.core.domain.Delivery
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class DeliveryCache {

    private val _items =
        MutableStateFlow<Map<Int, Delivery>>(emptyMap())

    val items: StateFlow<Map<Int, Delivery>> = _items.asStateFlow()

    fun putAll(items: List<Delivery>) {
        _items.update { current ->
            current + items.associateBy { it.id }
        }
    }

    fun update(id: Int, transform: (Delivery) -> Delivery) {
        _items.update { current ->
            val item = current[id] ?: return@update current
            current + (id to transform(item))
        }
    }
}