package com.github.radlance.autodispatch.profile.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.github.radlance.autodispatch.common.presentation.shimmerBackground

@Composable
fun DriverProfileShimmer(modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier.padding(horizontal = 18.dp).padding(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {

        item {
            Row(verticalAlignment = Alignment.CenterVertically) {

                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .shimmerBackground()
                )

                Spacer(Modifier.width(12.dp))

                Column {
                    Box(
                        modifier = Modifier
                            .height(16.dp)
                            .width(140.dp)
                            .shimmerBackground()
                    )
                    Spacer(Modifier.height(6.dp))
                    Box(
                        modifier = Modifier
                            .height(12.dp)
                            .width(100.dp)
                            .shimmerBackground()
                    )
                }
            }
        }

        item {
            Column(Modifier.fillMaxWidth().padding(vertical = 12.dp)) {

                Box(
                    modifier = Modifier
                        .height(18.dp)
                        .width(160.dp)
                        .shimmerBackground()
                )

                Spacer(Modifier.height(12.dp))

                LazyVerticalGrid(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 180.dp, max = 350.dp),
                    columns = GridCells.Fixed(2),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    repeat(5) { index ->
                        val isLast = index == 4
                        val isOdd = true
                        val span = if (isLast && isOdd) 2 else 1

                        item(span = { GridItemSpan(span) }) {
                            StatTileShimmer()
                        }
                    }
                }
            }
        }

        item {
            Card {
                Row(
                    modifier = Modifier
                        .padding(18.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .shimmerBackground()
                    )
                    Spacer(Modifier.width(12.dp))
                    Box(
                        modifier = Modifier
                            .height(18.dp)
                            .width(120.dp)
                            .shimmerBackground()
                    )
                }

                HorizontalDivider(Modifier.fillMaxWidth().padding(horizontal = 18.dp))

                Row(
                    modifier = Modifier
                        .padding(18.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .shimmerBackground()
                    )
                    Spacer(Modifier.width(16.dp))
                    Column {
                        Box(
                            modifier = Modifier
                                .height(16.dp)
                                .width(120.dp)
                                .shimmerBackground()
                        )
                        Spacer(Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .height(14.dp)
                                .width(60.dp)
                                .shimmerBackground()
                        )
                    }
                }

                HorizontalDivider(Modifier.fillMaxWidth().padding(horizontal = 18.dp))

                Row(
                    modifier = Modifier
                        .padding(18.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .shimmerBackground()
                    )
                    Spacer(Modifier.width(16.dp))
                    Column {
                        Box(
                            modifier = Modifier
                                .height(16.dp)
                                .width(110.dp)
                                .shimmerBackground()
                        )
                        Spacer(Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .height(14.dp)
                                .width(80.dp)
                                .shimmerBackground()
                        )
                    }
                }
            }
        }

        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .shimmerBackground()
            )
        }
    }
}

@Composable
private fun StatTileShimmer() {
    Card(Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .height(34.dp)
                    .width(32.dp)
                    .shimmerBackground()
            )
            Spacer(Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .height(12.dp)
                    .width(70.dp)
                    .shimmerBackground()
            )
        }
    }
}
