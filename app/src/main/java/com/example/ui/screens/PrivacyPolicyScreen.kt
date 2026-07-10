package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.GameThemes
import com.example.ui.components.AppBackground
import com.example.ui.viewmodel.GameViewModel

@Composable
fun PrivacyPolicyScreen(
    viewModel: GameViewModel,
    onNavigateBack: () -> Unit
) {
    val settingsState by viewModel.settings.collectAsState()
    val isDark = when (settingsState.darkMode.lowercase()) {
        "light" -> false
        "dark" -> true
        else -> androidx.compose.foundation.isSystemInDarkTheme()
    }
    val themeColors = GameThemes.getTheme(settingsState.selectedBoardTheme, isSystemDark = isDark)

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
                    text = "PRIVACY POLICY",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = themeColors.primaryText,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            // Body Scrollable
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = themeColors.surface)
                ) {
                    Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(
                            text = "PRIVACY ASSURANCE",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black,
                            color = themeColors.accent
                        )
                        
                        Text(
                            text = "1. No Data Collection: Tic-Tac-Toe Challenger operates entirely on your physical device. We do not collect, monitor, store, or transmit any personal identification, telemetry logs, or location data.\n\n" +
                                   "2. 100% Offline Integrity: Because the application executes all match flows, minimax AI processing, statistics logging, and achievements completely locally, no internet permission or network sync layers are ever initiated.\n\n" +
                                   "3. Secure Local Storage: All settings preferences, achievements, and game statistic logs are persisted securely using a local SQLite Room Database within the sandboxed application directory on your device, private to this app only.\n\n" +
                                   "4. No Third-Party SDKs: We do not integrate any analytical trackers, advertisements networks, or cloud platforms, ensuring your offline gaming privacy is absolute.",
                            fontSize = 14.sp,
                            color = themeColors.primaryText.copy(alpha = 0.8f),
                            lineHeight = 22.sp
                        )
                    }
                }
            }
        }
    }
}
