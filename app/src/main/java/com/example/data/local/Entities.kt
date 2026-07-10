package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "settings")
data class GameSettings(
    @PrimaryKey val id: Int = 1,
    val darkMode: String = "dark", // "system", "light", "dark"
    val soundEnabled: Boolean = true,
    val musicEnabled: Boolean = false,
    val vibrationEnabled: Boolean = true,
    val selectedBoardTheme: String = "glass", // "glass", "classic", "neon", "cyberpunk", "forest", "minimal"
    val selectedXStyle: String = "classic", // "classic", "neon", "brush", "pixel"
    val selectedOStyle: String = "classic", // "classic", "neon", "brush", "pixel"
    val selectedVictoryEffect: String = "confetti", // "confetti", "sparkles", "fireworks"
    val selectedButtonStyle: String = "rounded", // "rounded", "sharp", "floating"
    val lastSelectedMode: String = "pvp", // "pvp", "ai"
    val ultraResolution: Boolean = true,
    val useGeminiAi: Boolean = true
)

@Entity(tableName = "statistics")
data class GameStatistics(
    @PrimaryKey val id: Int = 1,
    val gamesPlayed: Int = 0,
    val winsPvP: Int = 0,
    val winsAI: Int = 0,
    val lossesAI: Int = 0,
    val draws: Int = 0,
    val longestWinStreak: Int = 0,
    val currentWinStreak: Int = 0,
    val totalMatchDurationSeconds: Long = 0
) {
    val winRate: Float
        get() = if (gamesPlayed > 0) ((winsPvP + winsAI).toFloat() / gamesPlayed) * 100f else 0f

    val averageMatchDurationSeconds: Long
        get() = if (gamesPlayed > 0) totalMatchDurationSeconds / gamesPlayed else 0L
}

@Entity(tableName = "achievements")
data class GameAchievement(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val isUnlocked: Boolean = false,
    val unlockedTimestamp: Long? = null
)
