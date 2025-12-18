package com.github.radlance.autodispatch.driver.common.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.radlance.autodispatch.common.presentation.DefaultPointerSelectionContainer

@Composable
fun DeliveryRoute(
    loadingPoint: String,
    unloadingPoint: String,
    modifier: Modifier = Modifier
) {
    DefaultPointerSelectionContainer {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min),
            verticalAlignment = Alignment.Top
        ) {
            Column(
                modifier = Modifier
                    .width(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.Circle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )

                Spacer(
                    modifier = Modifier
                        .weight(1f)
                        .width(2.dp)
                        .background(MaterialTheme.colorScheme.primary)
                )

                Icon(
                    imageVector = Icons.Default.Place,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Погрузка",
                        fontSize = 12.sp,
                        modifier = Modifier.alpha(0.7f)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = loadingPoint,
                        fontSize = 14.sp,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))

                Column {
                    Text(
                        text = "Разгрузка",
                        fontSize = 12.sp,
                        modifier = Modifier.alpha(0.7f)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = unloadingPoint,
                        fontSize = 14.sp,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}