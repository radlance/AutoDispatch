package com.github.radlance.autodispatch.request.core.presentation.core

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.LastPage
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.outlined.FirstPage
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun BottomPagingBar(
    start: Int,
    end: Int,
    totalCount: Int,
    pageIndex: Int,
    pageCount: Int,
    onFirst: () -> Unit,
    onPrev: () -> Unit,
    onNext: () -> Unit,
    onLast: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.End),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text("$start-$end из $totalCount")

        IconButton(onClick = onFirst, enabled = pageIndex > 0) {
            Icon(
                Icons.Outlined.FirstPage,
                null
            )
        }
        IconButton(onClick = onPrev, enabled = pageIndex > 0) {
            Icon(
                Icons.Default.ChevronLeft,
                null
            )
        }
        IconButton(
            onClick = onNext,
            enabled = pageIndex < pageCount - 1
        ) { Icon(Icons.Default.ChevronRight, null) }
        IconButton(
            onClick = onLast,
            enabled = pageIndex < pageCount - 1
        ) { Icon(Icons.AutoMirrored.Default.LastPage, null) }
    }
}
