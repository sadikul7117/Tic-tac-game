package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.domain.model.ThemeColors

@Composable
fun AppBackground(
    themeColors: ThemeColors,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                if (themeColors.isGlass) {
                    themeColors.background
                } else {
                    Color.Transparent
                }
            )
            .drawBehind {
                if (themeColors.isGlass) {
                    // Top-right ambient glowing purple circle
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                if (themeColors.isDark) Color(0x3B818CF8) else Color(0x45D8B4FE),
                                Color.Transparent
                            ),
                            center = Offset(size.width * 0.9f, size.height * -0.05f),
                            radius = size.width * 0.7f
                        ),
                        center = Offset(size.width * 0.9f, size.height * -0.05f),
                        radius = size.width * 0.7f
                    )

                    // Bottom-left ambient glowing blue circle
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                if (themeColors.isDark) Color(0x3B38BDF8) else Color(0x4593C5FD),
                                Color.Transparent
                            ),
                            center = Offset(size.width * 0.1f, size.height * 0.9f),
                            radius = size.width * 0.8f
                        ),
                        center = Offset(size.width * 0.1f, size.height * 0.9f),
                        radius = size.width * 0.8f
                    )
                }
            }
            .then(
                if (!themeColors.isGlass) {
                    Modifier.background(
                        brush = Brush.verticalGradient(
                            colors = listOf(themeColors.background, themeColors.surface)
                        )
                    )
                } else Modifier
            )
    ) {
        content()
    }
}

@Composable
fun GlassCard(
    themeColors: ThemeColors,
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 24.dp,
    content: @Composable ColumnScope.() -> Unit
) {
    if (themeColors.isGlass) {
        Box(
            modifier = modifier
                .clip(RoundedCornerShape(cornerRadius))
                .background(themeColors.surface)
                .border(
                    BorderStroke(1.dp, themeColors.boardLine),
                    shape = RoundedCornerShape(cornerRadius)
                )
                .padding(16.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                content()
            }
        }
    } else {
        Card(
            shape = RoundedCornerShape(cornerRadius),
            colors = CardDefaults.cardColors(containerColor = themeColors.surface),
            modifier = modifier,
            content = content
        )
    }
}
