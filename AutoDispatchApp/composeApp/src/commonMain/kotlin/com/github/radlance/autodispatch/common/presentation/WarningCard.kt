package com.github.radlance.autodispatch.common.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun WarningCard(
    icon: ImageVector,
    contentColor: Color,
    containerColor: Color,
    message: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxWidth().clip(CardDefaults.shape)
            .background(containerColor)
    ) {
        Column(Modifier.padding(horizontal = 18.dp, vertical = 12.dp)) {
            Row(verticalAlignment = Alignment.Top) {
                Box(Modifier.size(24.dp)) {
                    Icon(
                        icon,
                        contentDescription = null,
                        tint = contentColor
                    )
                }
                Spacer(Modifier.width(12.dp))
                Text(
                    text = message,
                    fontSize = 12.sp,
                    color = contentColor
                )
            }
        }
    }
}