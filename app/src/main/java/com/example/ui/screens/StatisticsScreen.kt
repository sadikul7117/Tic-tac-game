package com.example.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.GameThemes
import com.example.ui.components.AppBackground
import com.example.ui.viewmodel.GameViewModel

@Composable
fun StatisticsScreen(
    viewModel: GameViewModel,
    onNavigateBack: () -> Unit
) {
    val settingsState by viewModel.settings.collectAsState()
    val statisticsState by viewModel.statistics.collectAsState()
    val isDark = when (settingsState.darkMode.lowercase()) {
        "light" -> false
        "dark" -> true
        else -> androidx.compose.foundation.isSystemInDarkTheme()
    }
    val themeColors = GameThemes.getTheme(settingsState.selectedBoardTheme, isSystemDark = isDark)
    
    // Animate the circular win-rate progress
    val winRateAnimate = remember { Animatable(0f) }
    LaunchedEffect(statisticsState.winRate) {
        winRateAnimate.animateTo(
            targetValue = statisticsState.winRate / 100f,
            animationSpec = tween(1000)
        )
    }

    AppBackground(
        themeColors = themeColors
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        viewModel.triggerHapticFeedback()
                        onNavigateBack()
                    }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = themeColors.primaryText
                    )
                }
                Text(
                    text = "STATISTICS",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = themeColors.primaryText,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            // Scrollable Content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Top Circular Win Rate Widget
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = themeColors.surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier.size(150.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            // Circular Meter Canvas
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                val strokeWidth = 14.dp.toPx()
                                val sizeOfArc = size.width - strokeWidth
                                
                                // Track
                                drawArc(
                                    color = themeColors.boardLine.copy(alpha = 0.15f),
                                    startAngle = 135f,
                                    sweepAngle = 270f,
                                    useCenter = false,
                                    topLeft = Offset(strokeWidth/2, strokeWidth/2),
                                    size = Size(sizeOfArc, sizeOfArc),
                                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                                )
                                
                                // Accent Fill
                                drawArc(
                                    color = themeColors.accent,
                                    startAngle = 135f,
                                    sweepAngle = 270f * winRateAnimate.value,
                                    useCenter = false,
                                    topLeft = Offset(strokeWidth/2, strokeWidth/2),
                                    size = Size(sizeOfArc, sizeOfArc),
                                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                                )
                            }
                            
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "%.1f%%".format(statisticsState.winRate),
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Black,
                                    color = themeColors.primaryText
                                )
                                Text(
                                    text = "WIN RATE",
                                    fontSize = 11.sp,
                                    color = themeColors.accent,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            StatMiniBadge(
                                label = "PLAYED",
                                value = statisticsState.gamesPlayed.toString(),
                                color = themeColors.primaryText.copy(alpha = 0.8f)
                            )
                            StatMiniBadge(
                                label = "WINS",
                                value = (statisticsState.winsPvP + statisticsState.winsAI).toString(),
                                color = themeColors.xColor
                            )
                            StatMiniBadge(
                                label = "LOSSES",
                                value = statisticsState.lossesAI.toString(),
                                color = themeColors.oColor
                            )
                            StatMiniBadge(
                                label = "DRAWS",
                                value = statisticsState.draws.toString(),
                                color = themeColors.boardLine
                            )
                        }
                    }
                }

                // Grid Statistics detail list
                StatDetailItem(
                    title = "Player vs Player Wins",
                    value = statisticsState.winsPvP.toString(),
                    icon = Icons.Default.People,
                    color = themeColors.xColor,
                    themeColors = themeColors
                )

                StatDetailItem(
                    title = "Player vs AI Wins",
                    value = statisticsState.winsAI.toString(),
                    icon = Icons.Default.SmartToy,
                    color = themeColors.oColor,
                    themeColors = themeColors
                )

                StatDetailItem(
                    title = "Current Win Streak",
                    value = "${statisticsState.currentWinStreak} games",
                    icon = Icons.Default.StarHalf,
                    color = themeColors.accent,
                    themeColors = themeColors
                )

                StatDetailItem(
                    title = "Longest Win Streak",
                    value = "${statisticsState.longestWinStreak} games",
                    icon = Icons.Default.EmojiEvents,
                    color = Color(0xFFFFC107),
                    themeColors = themeColors
                )

                StatDetailItem(
                    title = "Average Match Duration",
                    value = "${statisticsState.averageMatchDurationSeconds} sec",
                    icon = Icons.Default.HourglassEmpty,
                    color = themeColors.primaryText.copy(alpha = 0.6f),
                    themeColors = themeColors
                )

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun StatMiniBadge(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            fontSize = 18.sp,
            fontWeight = FontWeight.ExtraBold,
            color = color
        )
        Text(
            text = label,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Gray,
            letterSpacing = 0.5.sp
        )
    }
}

@Composable
fun StatDetailItem(
    title: String,
    value: String,
    icon: ImageVector,
    color: Color,
    themeColors: com.example.domain.model.ThemeColors
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = themeColors.surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(color.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = color,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = themeColors.primaryText
                )
            }
            Text(
                text = value,
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold,
                color = themeColors.primaryText
            )
        }
    }
}
