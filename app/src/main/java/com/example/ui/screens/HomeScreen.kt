package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.GameThemes
import com.example.ui.components.AppBackground
import com.example.ui.components.DrawO
import com.example.ui.components.DrawX
import com.example.ui.viewmodel.GameViewModel

@Composable
fun HomeScreen(
    viewModel: GameViewModel,
    onNavigateToGame: (String) -> Unit, // "pvp" or "ai"
    onNavigateToSettings: () -> Unit,
    onNavigateToStats: () -> Unit,
    onNavigateToAchievements: () -> Unit,
    onNavigateToCustomization: () -> Unit,
    onNavigateToAbout: () -> Unit
) {
    val settingsState by viewModel.settings.collectAsState()
    val isDark = when (settingsState.darkMode.lowercase()) {
        "light" -> false
        "dark" -> true
        else -> androidx.compose.foundation.isSystemInDarkTheme()
    }
    val themeColors = GameThemes.getTheme(settingsState.selectedBoardTheme, isSystemDark = isDark)
    
    // Scale animation on load
    val scaleAnim = remember { Animatable(0.9f) }
    LaunchedEffect(Unit) {
        scaleAnim.animateTo(
            targetValue = 1f,
            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)
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
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .scale(scaleAnim.value),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // Premium Animated Logo Header
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.size(90.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.size(40.dp)) {
                        DrawX(
                            color = themeColors.xColor,
                            style = settingsState.selectedXStyle,
                            isUltra = settingsState.ultraResolution
                        )
                    }
                    Box(modifier = Modifier.size(40.dp)) {
                        DrawO(
                            color = themeColors.oColor,
                            style = settingsState.selectedOStyle,
                            isUltra = settingsState.ultraResolution
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "TIC-TAC-TOE",
                    fontSize = 32.sp,
                    color = themeColors.primaryText,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.SansSerif,
                    letterSpacing = 2.sp
                )
                Text(
                    text = "Challenger Edition",
                    fontSize = 11.sp,
                    color = themeColors.accent,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 1.sp,
                    modifier = Modifier.alpha(0.85f)
                )
            }

            Spacer(modifier = Modifier.height(30.dp))

            // Game Modes Section
            Text(
                text = "SELECT MATCH MODE",
                fontSize = 11.sp,
                color = themeColors.primaryText.copy(alpha = 0.5f),
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.SansSerif,
                letterSpacing = 1.5.sp,
                modifier = Modifier.align(Alignment.Start)
            )

            // Play AI Card (Master Mode)
            HomeMenuCard(
                title = "Player vs Perfect AI",
                subtitle = "Challenge the unbeatable Minimax machine.",
                icon = Icons.Default.SmartToy,
                accentColor = themeColors.xColor,
                themeColors = themeColors,
                onClick = {
                    viewModel.triggerHapticFeedback()
                    viewModel.selectGameMode("ai")
                    onNavigateToGame("ai")
                }
            )

            // Play PvP Card
            HomeMenuCard(
                title = "Player vs Player",
                subtitle = "Pass & Play with a friend offline.",
                icon = Icons.Default.People,
                accentColor = themeColors.oColor,
                themeColors = themeColors,
                onClick = {
                    viewModel.triggerHapticFeedback()
                    viewModel.selectGameMode("pvp")
                    onNavigateToGame("pvp")
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Navigation Grid
            Text(
                text = "DASHBOARD",
                fontSize = 11.sp,
                color = themeColors.primaryText.copy(alpha = 0.5f),
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.SansSerif,
                letterSpacing = 1.5.sp,
                modifier = Modifier.align(Alignment.Start)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    HomeGridItem(
                        title = "Customize",
                        icon = Icons.Default.Palette,
                        themeColors = themeColors,
                        onClick = {
                            viewModel.triggerHapticFeedback()
                            onNavigateToCustomization()
                        }
                    )
                }
                Box(modifier = Modifier.weight(1f)) {
                    HomeGridItem(
                        title = "Statistics",
                        icon = Icons.Default.Leaderboard,
                        themeColors = themeColors,
                        onClick = {
                            viewModel.triggerHapticFeedback()
                            onNavigateToStats()
                        }
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    HomeGridItem(
                        title = "Achievements",
                        icon = Icons.Default.EmojiEvents,
                        themeColors = themeColors,
                        onClick = {
                            viewModel.triggerHapticFeedback()
                            onNavigateToAchievements()
                        }
                    )
                }
                Box(modifier = Modifier.weight(1f)) {
                    HomeGridItem(
                        title = "Settings",
                        icon = Icons.Default.Settings,
                        themeColors = themeColors,
                        onClick = {
                            viewModel.triggerHapticFeedback()
                            onNavigateToSettings()
                        }
                    )
                }
            }

            // About Button
            Button(
                onClick = {
                    viewModel.triggerHapticFeedback()
                    onNavigateToAbout()
                },
                colors = ButtonDefaults.buttonColors(containerColor = themeColors.surface.copy(alpha = 0.6f)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "About",
                    tint = themeColors.primaryText.copy(alpha = 0.7f),
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "About & Game Rules",
                    color = themeColors.primaryText.copy(alpha = 0.8f),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun HomeMenuCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color,
    themeColors: com.example.domain.model.ThemeColors,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = themeColors.surface),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(accentColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(28.dp)
                )
            }
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = themeColors.primaryText
                )
                Text(
                    text = subtitle,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Normal,
                    color = themeColors.primaryText.copy(alpha = 0.65f)
                )
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = themeColors.primaryText.copy(alpha = 0.3f),
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun HomeGridItem(
    title: String,
    icon: ImageVector,
    themeColors: com.example.domain.model.ThemeColors,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = themeColors.surface),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = themeColors.accent,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = themeColors.primaryText
            )
        }
    }
}
