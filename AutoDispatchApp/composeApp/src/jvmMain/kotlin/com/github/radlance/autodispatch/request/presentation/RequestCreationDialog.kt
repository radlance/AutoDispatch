package com.github.radlance.autodispatch.request.presentation

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun RequestCreationDialog(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    AlertDialog(
        modifier = modifier,
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Создание новой заявки",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 12.dp)
            )
        },
        text = {
            Box(modifier = Modifier.fillMaxWidth().heightIn(max = 400.dp)) {
                Column(modifier = Modifier.verticalScroll(scrollState)) {
                    CustomDropDownMenu(
                        label = "Откуда",
                        selectedOption = null,
                        filterOptions = listOf("1", "2", "3", "4", "5"),
                        onOptionSelected = {},
                        hint = "Выберите город"
                    )
                }
                VerticalScrollbar(
                    adapter = rememberScrollbarAdapter(scrollState),
                    modifier = Modifier
                        .fillMaxHeight()
                        .align(Alignment.CenterEnd)
                        .offset(x = 10.dp)
                )
            }
        },
        confirmButton = {

        },
        dismissButton = {

        },
        shape = RoundedCornerShape(16.dp)
    )
}