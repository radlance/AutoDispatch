package com.github.radlance.autodispatch.delivery.details.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.github.radlance.autodispatch.common.presentation.shimmerBackground

@Composable
fun DeliveryDetailsShimmer(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 14.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        StatusCardShimmer()
        RouteCardShimmer()
        CargoCardShimmer()
        VehicleCardShimmer()
        ContactsCardShimmer()
        AdditionalInfoCardShimmer()
        ActionButtonsShimmer()
    }
}

@Composable
private fun StatusCardShimmer() {
    Card(
        modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(
            containerColor = CardDefaults.cardColors().containerColor.copy(
                alpha = 0.3f
            )
        )
    ) {
        Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 26.dp)) {
            Box(
                modifier = Modifier
                    .height(22.dp)
                    .width(180.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .shimmerBackground()
            )
            Spacer(Modifier.height(12.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .shimmerBackground()
                    .height(36.dp)
                    .fillMaxWidth(0.35f)
            )
            Spacer(Modifier.height(18.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .shimmerBackground()
                )
                Spacer(Modifier.width(12.dp))
                Column {
                    Box(
                        modifier = Modifier
                            .height(14.dp)
                            .width(160.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .shimmerBackground()
                    )
                    Spacer(Modifier.height(6.dp))
                    Box(
                        modifier = Modifier
                            .height(12.dp)
                            .width(120.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .shimmerBackground()
                    )
                }
            }
        }
    }
}

@Composable
private fun RouteCardShimmer() {
    Card(
        modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(
            containerColor = CardDefaults.cardColors().containerColor.copy(
                alpha = 0.3f
            )
        )
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.001f))
                    .padding(18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .shimmerBackground()
                )
                Spacer(Modifier.width(12.dp))
                Box(
                    modifier = Modifier
                        .height(16.dp)
                        .width(140.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .shimmerBackground()
                )
            }

            Column(modifier = Modifier.padding(18.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(
                        modifier = Modifier
                            .width(24.dp)
                            .height(135.dp),
                        verticalArrangement = Arrangement.SpaceBetween,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(16.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .shimmerBackground()
                        )
                        Box(
                            modifier = Modifier
                                .width(2.dp)
                                .height(80.dp)
                                .shimmerBackground()
                        )
                        Box(
                            modifier = Modifier
                                .size(16.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .shimmerBackground()
                        )
                    }

                    Spacer(Modifier.width(12.dp))

                    Column {
                        Box(
                            modifier = Modifier
                                .height(14.dp)
                                .fillMaxWidth(0.85f)
                                .clip(RoundedCornerShape(4.dp))
                                .shimmerBackground()
                        )
                        Spacer(Modifier.height(100.dp))
                        Box(
                            modifier = Modifier
                                .height(14.dp)
                                .fillMaxWidth(0.7f)
                                .clip(RoundedCornerShape(4.dp))
                                .shimmerBackground()
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CargoCardShimmer() {
    Card(
        modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(
            containerColor = CardDefaults.cardColors().containerColor.copy(
                alpha = 0.3f
            )
        )
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.001f))
                    .padding(18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .shimmerBackground()
                )
                Spacer(Modifier.width(12.dp))
                Box(
                    modifier = Modifier
                        .height(16.dp)
                        .width(160.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .shimmerBackground()
                )
            }

            Column(modifier = Modifier.padding(18.dp)) {
                Box(
                    modifier = Modifier
                        .height(12.dp)
                        .width(120.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .shimmerBackground()
                )
                Spacer(Modifier.height(8.dp))

                Row {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .shimmerBackground()
                    )
                    Spacer(Modifier.width(12.dp))
                    Box(
                        modifier = Modifier
                            .height(14.dp)
                            .width(160.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .shimmerBackground()
                    )
                }

                Spacer(Modifier.height(18.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(18.dp)) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .height(82.dp)
                            .shimmerBackground()
                    )
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .height(82.dp)
                            .shimmerBackground()
                    )
                }

                Spacer(Modifier.height(18.dp))

                Box(
                    modifier = Modifier
                        .height(56.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .shimmerBackground()
                )
            }
        }
    }
}

@Composable
private fun VehicleCardShimmer() {
    Card(
        modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(
            containerColor = CardDefaults.cardColors().containerColor.copy(
                alpha = 0.3f
            )
        )
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .shimmerBackground()
                )
                Spacer(Modifier.width(12.dp))
                Box(
                    modifier = Modifier
                        .height(16.dp)
                        .width(120.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .shimmerBackground()
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(18.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .shimmerBackground()
                )
                Spacer(Modifier.width(12.dp))
                Column {
                    Box(
                        modifier = Modifier
                            .height(14.dp)
                            .width(160.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .shimmerBackground()
                    )
                    Spacer(Modifier.height(6.dp))
                    Box(
                        modifier = Modifier
                            .height(12.dp)
                            .width(120.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .shimmerBackground()
                    )
                }
            }
        }
    }
}

@Composable
private fun ContactsCardShimmer() {
    Card(
        modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(
            containerColor = CardDefaults.cardColors().containerColor.copy(
                alpha = 0.3f
            )
        )
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth().padding(18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier.size(20.dp).clip(RoundedCornerShape(4.dp))
                        .shimmerBackground()
                )
                Spacer(Modifier.width(12.dp))
                Box(
                    modifier = Modifier.height(16.dp).width(140.dp).clip(RoundedCornerShape(4.dp))
                        .shimmerBackground()
                )
            }

            Column(modifier = Modifier.padding(18.dp)) {
                Box(
                    modifier = Modifier.height(12.dp).width(120.dp).clip(RoundedCornerShape(4.dp))
                        .shimmerBackground()
                )
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier.size(40.dp).clip(RoundedCornerShape(12.dp))
                            .shimmerBackground()
                    )
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Box(
                            modifier = Modifier.height(14.dp).width(160.dp)
                                .clip(RoundedCornerShape(4.dp)).shimmerBackground()
                        )
                        Spacer(Modifier.height(6.dp))
                        Box(
                            modifier = Modifier.height(12.dp).width(120.dp)
                                .clip(RoundedCornerShape(4.dp)).shimmerBackground()
                        )
                    }
                }

                Spacer(Modifier.height(18.dp))

                Box(
                    modifier = Modifier.height(12.dp).width(120.dp).clip(RoundedCornerShape(4.dp))
                        .shimmerBackground()
                )
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier.size(40.dp).clip(RoundedCornerShape(12.dp))
                            .shimmerBackground()
                    )
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Box(
                            modifier = Modifier.height(14.dp).width(160.dp)
                                .clip(RoundedCornerShape(4.dp)).shimmerBackground()
                        )
                        Spacer(Modifier.height(6.dp))
                        Box(
                            modifier = Modifier.height(12.dp).width(120.dp)
                                .clip(RoundedCornerShape(4.dp)).shimmerBackground()
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AdditionalInfoCardShimmer() {
    Card(
        modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(
            containerColor = CardDefaults.cardColors().containerColor.copy(
                alpha = 0.3f
            )
        )
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth().padding(18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier.size(20.dp).clip(RoundedCornerShape(4.dp))
                        .shimmerBackground()
                )
                Spacer(Modifier.width(12.dp))
                Box(
                    modifier = Modifier.height(16.dp).width(200.dp).clip(RoundedCornerShape(4.dp))
                        .shimmerBackground()
                )
            }

            Row(
                modifier = Modifier.padding(18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier.height(14.dp).fillMaxWidth().clip(RoundedCornerShape(6.dp))
                        .shimmerBackground()
                )
            }

            Spacer(Modifier.height(12.dp))
        }
    }
}

@Composable
private fun ActionButtonsShimmer() {
    Column(
        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .height(48.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .shimmerBackground()
        )
        Box(
            modifier = Modifier
                .height(48.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .shimmerBackground()
        )
    }
}
