package com.github.radlance.autodispatch.request.presentation.core

import androidx.compose.foundation.HorizontalScrollbar
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.seanproctor.datatable.DataColumn
import com.seanproctor.datatable.DataTableScope
import com.seanproctor.datatable.DataTableState
import com.seanproctor.datatable.material3.Material3CellContentProvider

@Composable
fun CustomPaginationDataTable(
    columns: List<DataColumn>,
    modifier: Modifier = Modifier,
    separator: @Composable () -> Unit = { HorizontalDivider() },
    dataTableState: DataTableState = remember { DataTableState() },
    headerHeight: Dp = 56.dp,
    rowHeight: Dp = 52.dp,
    contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp),
    headerBackgroundColor: Color = MaterialTheme.colorScheme.surface,
    footerBackgroundColor: Color = MaterialTheme.colorScheme.surface,
    sortColumnIndex: Int? = null,
    sortAscending: Boolean = true,
    logger: ((String) -> Unit)? = null,
    content: DataTableScope.() -> Unit,
) {
    Column(modifier = modifier.fillMaxWidth().padding(end = 16.dp)) {
        Box {
            CustomPaginatedDataTable(
                dataTableState = dataTableState,
                columns = columns,
                modifier = Modifier.fillMaxWidth(),
                separator = separator,
                headerHeight = headerHeight,
                rowHeight = rowHeight,
                contentPadding = contentPadding,
                headerBackgroundColor = headerBackgroundColor,
                footerBackgroundColor = footerBackgroundColor,
                footer = {},
                cellContentProvider = Material3CellContentProvider,
                sortColumnIndex = sortColumnIndex,
                sortAscending = sortAscending,
                logger = logger,
                content = content
            )
                HorizontalScrollbar(
                    modifier = Modifier
                        .fillMaxWidth().align(Alignment.BottomCenter),
                    adapter = rememberDataTableScrollbarAdapter(
                        scrollState = dataTableState.horizontalScrollState
                    )
                )
        }
    }
}