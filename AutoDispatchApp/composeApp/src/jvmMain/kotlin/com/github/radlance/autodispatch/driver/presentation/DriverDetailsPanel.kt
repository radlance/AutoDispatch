package com.github.radlance.autodispatch.driver.presentation

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.radlance.autodispatch.driver.domain.Driver

@Composable
fun DriverDetailsPanel(
    driver: Driver,
    onClosePanel: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(modifier = modifier.padding(8.dp)) {
        DriverPanelHeader(onClose = onClosePanel)

        Box {
            DriverDetailsSections(
                scrollState = scrollState,
                driver = driver
            )
            VerticalScrollbar(
                modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight().offset(x = 3.dp),
                adapter = rememberScrollbarAdapter(scrollState)
            )
        }
    }
}