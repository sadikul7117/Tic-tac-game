package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.GameThemes
import com.example.ui.components.AppBackground
import com.example.ui.viewmodel.GameViewModel

@Composable
fun AboutScreen(
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
                    text = "ABOUT & RULES",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = themeColors.primaryText,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            // Body content scrollable
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Game Rules Card
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = themeColors.surface)
                ) {
                    Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "HOW TO PLAY",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black,
                            color = themeColors.xColor
                        )
                        Text(
                            text = "1. The game is played on a classic grid that is 3 squares by 3 squares.\n\n" +
                                   "2. Player 1 is X, and the opponent (another player or the AI) is O. Players take turns putting their marks in empty squares.\n\n" +
                                   "3. The first player to get 3 of their marks in a row (horizontally, vertically, or diagonally) is the winner.\n\n" +
                                   "4. When all 9 squares are full and no player has 3 in a row, the game ends in a Draw.",
                            fontSize = 14.sp,
                            color = themeColors.primaryText.copy(alpha = 0.8f),
                            lineHeight = 20.sp
                        )
                    }
                }

                // AI Explanation Card
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = themeColors.surface)
                ) {
                    Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "PERFECT AI ENGINE",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black,
                            color = themeColors.oColor
                        )
                        Text(
                            text = "This application features a mathematically unbeatable offline Artificial Intelligence opponent.\n\n" +
                                   "The AI calculates optimal moves in real-time utilizing the Minimax Decision-Tree Search Algorithm. It evaluates all possible game branch paths to guarantee victory or force a defensive draw when winning is mathematically impossible.\n\n" +
                                   "To ensure instant gameplay performance and low memory footprints, we integrated Alpha-Beta Pruning. This optimization prunes redundant search branches, calculating moves in milliseconds.",
                            fontSize = 14.sp,
                            color = themeColors.primaryText.copy(alpha = 0.8f),
                            lineHeight = 20.sp
                        )
                    }
                }

                // App version card
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = themeColors.surface.copy(alpha = 0.5f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Tic-Tac-Toe Challenger",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = themeColors.primaryText
                        )
                        Text(
                            text = "Version 1.0.0 (Offline Native Edition)",
                            fontSize = 12.sp,
                            color = themeColors.primaryText.copy(alpha = 0.5f),
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
            }
        }
    }
}
