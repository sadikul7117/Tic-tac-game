package com.example.ui.screens

import android.text.format.DateFormat
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.GameAchievement
import com.example.domain.model.GameThemes
import com.example.ui.components.AppBackground
import com.example.ui.viewmodel.GameViewModel
import java.util.*

@Composable
fun AchievementsScreen(
    viewModel: GameViewModel,
    onNavigateBack: () -> Unit
) {
    val settingsState by viewModel.settings.collectAsState()
    val achievements by viewModel.achievements.collectAsState()
    val isDark = when (settingsState.darkMode.lowercase()) {
        "light" -> false
        "dark" -> true
        else -> androidx.compose.foundation.isSystemInDarkTheme()
    }
    val themeColors = GameThemes.getTheme(settingsState.selectedBoardTheme, isSystemDark = isDark)
    
    val unlockedCount = achievements.count { it.isUnlocked }

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
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
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
                        text = "ACHIEVEMENTS",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = themeColors.primaryText,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }

                // Locked/Unlocked counter tag
                Box(
                    modifier = Modifier
                        .padding(end = 16.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(themeColors.accent.copy(alpha = 0.15f))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "$unlockedCount / ${achievements.size} MEDALS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = themeColors.accent
                    )
                }
            }

            // Lazy Column Achievement List
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(achievements) { ach ->
                    AchievementItemCard(
                        achievement = ach,
                        themeColors = themeColors
                    )
                }
            }
        }
    }
}

@Composable
fun AchievementItemCard(
    achievement: GameAchievement,
    themeColors: com.example.domain.model.ThemeColors
) {
    val cardColor = if (achievement.isUnlocked) {
        themeColors.surface
    } else {
        themeColors.surface.copy(alpha = 0.5f)
    }

    val iconColor = if (achievement.isUnlocked) {
        Color(0xFFFFC107) // Gold
    } else {
        themeColors.boardLine.copy(alpha = 0.5f)
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Medal / Lock Icon Box
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        if (achievement.isUnlocked) Color(0xFFFFC107).copy(alpha = 0.12f)
                        else themeColors.boardLine.copy(alpha = 0.08f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (achievement.isUnlocked) Icons.Default.EmojiEvents else Icons.Default.Lock,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            // Content text
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = achievement.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (achievement.isUnlocked) themeColors.primaryText else themeColors.primaryText.copy(alpha = 0.45f)
                )
                Text(
                    text = achievement.description,
                    fontSize = 13.sp,
                    color = if (achievement.isUnlocked) themeColors.primaryText.copy(alpha = 0.65f) else themeColors.primaryText.copy(alpha = 0.35f),
                    modifier = Modifier.padding(top = 2.dp)
                )

                // Unlock timestamp
                if (achievement.isUnlocked && achievement.unlockedTimestamp != null) {
                    val dateString = DateFormat.format("MMM dd, yyyy h:mm a", Date(achievement.unlockedTimestamp)).toString()
                    Text(
                        text = "Unlocked on: $dateString",
                        fontSize = 10.sp,
                        color = themeColors.accent.copy(alpha = 0.8f),
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(top = 6.dp)
                    )
                }
            }
        }
    }
}
