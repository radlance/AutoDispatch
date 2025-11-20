package com.github.radlance.autodispatch.request.core.presentation.core

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.LastPage
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.ExpandLess
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.material.icons.outlined.FirstPage
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import autodispatch.composeapp.generated.resources.Res
import autodispatch.composeapp.generated.resources.by_page
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomPagingBar(
    start: Int,
    end: Int,
    totalCount: Int,
    pageIndex: Int,
    pageCount: Int,
    pageSize: Int,
    pageSizeOptions: List<Int>,
    onFirst: () -> Unit,
    onPrev: () -> Unit,
    onNext: () -> Unit,
    onLast: () -> Unit,
    onRefresh: () -> Unit,
    onPageSizeChange: (Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("$start-$end из $totalCount")

            Spacer(Modifier.width(36.dp))
            Text(text = stringResource(Res.string.by_page))
            Spacer(Modifier.width(12.dp))
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
                modifier = Modifier.clip(RoundedCornerShape(16.dp))
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .height(TextFieldDefaults.MinHeight)
                        .padding(vertical = 8.dp)
                        .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                        .clip(RoundedCornerShape(8.dp))
                        .background(SearchBarDefaults.colors().containerColor)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = ripple()
                        ) {}
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .width(100.dp)
                            .padding(horizontal = 12.dp)
                    ) {
                        Text(
                            text = pageSize.toString(),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.Center
                        )
                        Spacer(Modifier.width(18.dp))
                        val trailingIcon = if (expanded) {
                            Icons.Outlined.ExpandLess
                        } else Icons.Outlined.ExpandMore
                        Icon(
                            imageVector = trailingIcon, contentDescription = null
                        )
                    }
                }

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = {
                        expanded = false
                    },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.animateContentSize()
                ) {

                    pageSizeOptions.forEach { size ->
                        DropdownMenuItem(
                            text = {
                                Text(text = size.toString())
                            },
                            onClick = {
                                if (pageSize != size) {
                                    onPageSizeChange(size)
                                }
                                expanded = false
                            },
                            trailingIcon = {
                                if (size == pageSize) {
                                    Icon(
                                        imageVector = Icons.Outlined.Check,
                                        contentDescription = null
                                    )
                                }
                            },
                        )
                    }
                }
            }

        }



        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onRefresh) {
                Icon(
                    Icons.Default.Refresh,
                    contentDescription = null
                )
            }

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
}