package com.github.radlance.autodispatch.request.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.github.radlance.autodispatch.common.presentation.shimmerBackground


@Composable
fun RequestCardShimmer(modifier: Modifier = Modifier) {
    androidx.compose.material3.Card(
        modifier = modifier
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = CardDefaults.cardColors().containerColor.copy(
                alpha = 0.3f
            )
        )
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(18.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(55.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .shimmerBackground(12.dp)
                )
                Spacer(Modifier.width(8.dp))
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 18.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .height(16.dp)
                            .fillMaxWidth(0.5f)
                            .shimmerBackground()
                    )
                    Spacer(Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .height(20.dp)
                            .width(80.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .shimmerBackground(8.dp)
                    )
                }
                Spacer(Modifier.width(10.dp))
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .shimmerBackground()
                )
            }

            Spacer(Modifier.height(12.dp))

            Row(modifier = Modifier.padding(horizontal = 18.dp)) {
                Column(
                    modifier = Modifier
                        .height(130.dp)
                        .width(24.dp),
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(modifier = Modifier.size(16.dp).shimmerBackground())
                    Box(modifier = Modifier.height(90.dp).width(2.dp).shimmerBackground())
                    Box(modifier = Modifier.size(20.dp).shimmerBackground())
                }
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.height(130.dp)) {
                    Box(
                        modifier = Modifier
                            .height(14.dp)
                            .fillMaxWidth(0.8f)
                            .shimmerBackground()
                    )
                    Spacer(Modifier.weight(1f))
                    Box(
                        modifier = Modifier
                            .height(14.dp)
                            .fillMaxWidth(0.8f)
                            .shimmerBackground()
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp)
                    .offset(y = 2.dp)
                    .shimmerBackground()
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.03f))
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    repeat(3) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .height(14.dp)
                                    .width(40.dp)
                                    .shimmerBackground()
                            )
                            Spacer(Modifier.height(6.dp))
                            Box(
                                modifier = Modifier
                                    .height(16.dp)
                                    .width(60.dp)
                                    .shimmerBackground()
                            )
                        }
                    }
                }
            }
        }
    }
}