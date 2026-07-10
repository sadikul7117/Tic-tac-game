package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.GameThemes
import com.example.ui.components.AppBackground
import com.example.ui.viewmodel.GameViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: GameViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToAbout: () -> Unit,
    onNavigateToPrivacy: () -> Unit
) {
    val settingsState by viewModel.settings.collectAsState()
    val isDark = when (settingsState.darkMode.lowercase()) {
        "light" -> false
        "dark" -> true
        else -> androidx.compose.foundation.isSystemInDarkTheme()
    }
    val themeColors = GameThemes.getTheme(settingsState.selectedBoardTheme, isSystemDark = isDark)
    
    var showResetDialog by remember { mutableStateOf(false) }

    AppBackground(
        themeColors = themeColors
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            // Header Bar
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
                    text = "SETTINGS",
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
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Audio & Vibration Section Title
                SettingsSectionHeader(title = "AUDIO & FEEDBACK", color = themeColors.xColor)

                // Sound Effects Toggle
                SettingsToggleItem(
                    title = "Sound Effects",
                    subtitle = "Procedural audio click & placement sounds.",
                    icon = Icons.Default.VolumeUp,
                    checked = settingsState.soundEnabled,
                    onCheckedChange = { viewModel.updateSoundSetting(it) },
                    themeColors = themeColors
                )

                // Background Music Toggle
                SettingsToggleItem(
                    title = "Ambient Music",
                    subtitle = "Procedural, calming chiptune retro background track.",
                    icon = Icons.Default.MusicNote,
                    checked = settingsState.musicEnabled,
                    onCheckedChange = { viewModel.updateMusicSetting(it) },
                    themeColors = themeColors
                )

                // Vibration Toggle
                SettingsToggleItem(
                    title = "Haptic Feedback",
                    subtitle = "Gentle vibrations on tapping pieces & actions.",
                    icon = Icons.Default.Vibration,
                    checked = settingsState.vibrationEnabled,
                    onCheckedChange = { viewModel.updateVibrationSetting(it) },
                    themeColors = themeColors
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Graphics & Quality Section Title
                SettingsSectionHeader(title = "GRAPHICS & RENDERING", color = themeColors.accent)

                // 1080p Ultra Realistic Resolution Toggle
                SettingsToggleItem(
                    title = "1080p Ultra Realistic Resolution",
                    subtitle = "Enable anti-aliased curves, gloss shaders, dynamic reflections, and sub-pixel lighting.",
                    icon = Icons.Default.HighQuality,
                    checked = settingsState.ultraResolution,
                    onCheckedChange = { viewModel.updateUltraResolutionSetting(it) },
                    themeColors = themeColors
                )

                Spacer(modifier = Modifier.height(12.dp))

                // AI Configuration Section
                SettingsSectionHeader(title = "AI CONFIGURATION", color = themeColors.xColor)

                // Gemini Low-Latency Smart AI Toggle
                SettingsToggleItem(
                    title = "Gemini Low-Latency Smart AI",
                    subtitle = "Engage the advanced gemini-3.1-flash-lite model for snappy, strategic responses and live tactical thoughts.",
                    icon = Icons.Default.SmartToy,
                    checked = settingsState.useGeminiAi,
                    onCheckedChange = { viewModel.updateUseGeminiAiSetting(it) },
                    themeColors = themeColors
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Theme & Appearance Section Title
                SettingsSectionHeader(title = "THEME & MODE", color = themeColors.oColor)

                // Theme mode selection row (System / Light / Dark)
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = themeColors.surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Brightness4,
                                contentDescription = null,
                                tint = themeColors.accent,
                                modifier = Modifier.size(24.dp)
                            )
                            Text(
                                text = "App Theme Mode",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = themeColors.primaryText
                            )
                        }
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("light", "dark").forEach { mode ->
                                val isSelected = settingsState.darkMode == mode
                                Button(
                                    onClick = {
                                        viewModel.updateDarkModeSetting(mode)
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (isSelected) themeColors.accent else themeColors.surface.copy(alpha = 0.5f),
                                        contentColor = if (isSelected) Color.White else themeColors.primaryText.copy(alpha = 0.7f)
                                    ),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .border(
                                            width = if (isSelected) 0.dp else 1.dp,
                                            color = themeColors.boardLine.copy(alpha = 0.2f),
                                            shape = RoundedCornerShape(10.dp)
                                        )
                                ) {
                                    Text(
                                        text = mode.uppercase(),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Utilities & Maintenance Title
                SettingsSectionHeader(title = "SYSTEM & STATS", color = themeColors.accent)

                // Reset Stats button
                SettingsActionItem(
                    title = "Reset Statistics & Achievements",
                    subtitle = "Erase all offline wins, streaks, and unlocked medals.",
                    icon = Icons.Default.DeleteForever,
                    color = Color(0xFFEF4444),
                    themeColors = themeColors,
                    onClick = { showResetDialog = true }
                )

                // About Screen Button
                SettingsActionItem(
                    title = "About Tic-Tac-Toe",
                    subtitle = "Game guidelines, algorithms, and developer notes.",
                    icon = Icons.Default.Info,
                    themeColors = themeColors,
                    onClick = { onNavigateToAbout() }
                )

                // Privacy Policy Screen Button
                SettingsActionItem(
                    title = "Privacy Policy",
                    subtitle = "View offline application privacy terms.",
                    icon = Icons.Default.PrivacyTip,
                    themeColors = themeColors,
                    onClick = { onNavigateToPrivacy() }
                )

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    // Modal dialogue confirming reset of statistics
    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = {
                Text(
                    text = "Reset Statistics?",
                    fontWeight = FontWeight.Bold,
                    color = themeColors.primaryText
                )
            },
            text = {
                Text(
                    text = "This will clear all local records, games played, longest win streaks, and achievements permanently. This operation cannot be undone.",
                    color = themeColors.primaryText.copy(alpha = 0.7f)
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.resetAllStatisticsAndAchievements()
                        showResetDialog = false
                    }
                ) {
                    Text(text = "RESET ALL", color = Color(0xFFEF4444), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text(text = "CANCEL", color = themeColors.primaryText.copy(alpha = 0.6f))
                }
            },
            containerColor = themeColors.surface,
            shape = RoundedCornerShape(20.dp)
        )
    }
}

@Composable
fun SettingsSectionHeader(title: String, color: Color) {
    Text(
        text = title,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        color = color,
        fontFamily = FontFamily.SansSerif,
        letterSpacing = 1.5.sp,
        modifier = Modifier.padding(bottom = 4.dp)
    )
}

@Composable
fun SettingsToggleItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
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
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(themeColors.boardLine.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = themeColors.accent,
                    modifier = Modifier.size(20.dp)
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = themeColors.primaryText
                )
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = themeColors.primaryText.copy(alpha = 0.55f)
                )
            }
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = themeColors.accent,
                    checkedTrackColor = themeColors.accent.copy(alpha = 0.3f),
                    uncheckedThumbColor = themeColors.boardLine,
                    uncheckedTrackColor = themeColors.surface
                )
            )
        }
    }
}

@Composable
fun SettingsActionItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    color: Color? = null,
    themeColors: com.example.domain.model.ThemeColors,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = themeColors.surface),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background((color ?: themeColors.boardLine).copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color ?: themeColors.accent,
                    modifier = Modifier.size(20.dp)
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = color ?: themeColors.primaryText
                )
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = themeColors.primaryText.copy(alpha = 0.55f)
                )
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = themeColors.primaryText.copy(alpha = 0.3f),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
