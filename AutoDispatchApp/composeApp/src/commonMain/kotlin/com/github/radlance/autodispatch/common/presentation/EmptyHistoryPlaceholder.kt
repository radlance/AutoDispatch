package com.github.radlance.autodispatch.common.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun EmptyHistoryPlaceholder(modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
    ) {
        Icon(
            imageVector = Icons.Default.History,
            contentDescription = null,
            modifier = Modifier.size(64.dp).alpha(0.7f)
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "История доставок пуста",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Medium
            ),
            modifier = Modifier.alpha(0.7f)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Здесь появятся завершённые и отменённые доставки",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.alpha(0.5f)
        )
    }
}