package com.github.radlance.autodispatch.request.presentation.core

import androidx.compose.foundation.v2.ScrollbarAdapter
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.seanproctor.datatable.DataTableScrollState
import kotlin.math.max

@Composable
fun rememberDataTableScrollbarAdapter(
    scrollState: DataTableScrollState
): ScrollbarAdapter = remember(scrollState) {
    object : ScrollbarAdapter {

        override val scrollOffset: Double
            get() = scrollState.offset.toDouble()

        override val contentSize: Double
            get() = scrollState.totalSize.toDouble()

        override val viewportSize: Double
            get() = scrollState.viewportSize.toDouble()

        override suspend fun scrollTo(scrollOffset: Double) {
            val target = scrollOffset.toInt().coerceIn(
                0,
                max(0, scrollState.totalSize - scrollState.viewportSize)
            )
            scrollState.scrollTo(target)
        }
    }
}