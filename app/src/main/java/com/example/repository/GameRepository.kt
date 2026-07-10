package com.example.repository

import com.example.data.local.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

class GameRepository(private val database: AppDatabase) {

    private val settingsDao = database.settingsDao()
    private val statisticsDao = database.statisticsDao()
    private val achievementsDao = database.achievementsDao()

    val settings: Flow<GameSettings> = settingsDao.getSettingsFlow()
        .map { it ?: GameSettings() }

    val statistics: Flow<GameStatistics> = statisticsDao.getStatisticsFlow()
        .map { it ?: GameStatistics() }

    val achievements: Flow<List<GameAchievement>> = achievementsDao.getAchievementsFlow()

    // Seeds initial data if not present
    suspend fun initializeDatabase() {
        // Seed default settings
        if (settingsDao.getSettings() == null) {
            settingsDao.saveSettings(GameSettings())
        }

        // Seed default stats
        if (statisticsDao.getStatistics() == null) {
            statisticsDao.saveStatistics(GameStatistics())
        }

        // Seed achievements list
        val defaultAchievements = listOf(
            GameAchievement("first_win", "First Win", "Win your very first match of Tic-Tac-Toe."),
            GameAchievement("10_wins", "10 Wins", "Win 10 matches total."),
            GameAchievement("50_wins", "50 Wins", "Win 50 matches total."),
            GameAchievement("100_wins", "100 Wins", "Win 100 matches total."),
            GameAchievement("perfect_player", "Perfect Player", "Defeat the Perfect AI without a single loss in the session."),
            GameAchievement("unstoppable", "Unstoppable", "Reach a consecutive winning streak of 5 games."),
            GameAchievement("master_strategist", "Master Strategist", "Defeat your opponent in 5 or fewer moves."),
            GameAchievement("ai_challenger", "AI Challenger", "Play 5 matches against the Perfect AI.")
        )
        achievementsDao.insertAchievements(defaultAchievements)
    }

    suspend fun saveSettings(settings: GameSettings) {
        settingsDao.saveSettings(settings)
    }

    suspend fun recordMatchResult(
        isAiMode: Boolean,
        winnerSymbol: String?, // "X", "O", or null for Draw
        playerSymbol: String,  // Player's symbol in AI Mode, usually "X"
        durationSeconds: Long,
        movesCount: Int
    ) {
        val currentStats = statisticsDao.getStatistics() ?: GameStatistics()
        
        val newGamesPlayed = currentStats.gamesPlayed + 1
        var newWinsPvP = currentStats.winsPvP
        var newWinsAI = currentStats.winsAI
        var newLossesAI = currentStats.lossesAI
        var newDraws = currentStats.draws
        var newCurrentWinStreak = currentStats.currentWinStreak

        val won: Boolean
        if (winnerSymbol == null) {
            // Draw
            newDraws++
            newCurrentWinStreak = 0
            won = false
        } else if (isAiMode) {
            if (winnerSymbol == playerSymbol) {
                newWinsAI++
                newCurrentWinStreak++
                won = true
            } else {
                newLossesAI++
                newCurrentWinStreak = 0
                won = false
            }
        } else {
            // PvP Mode: just record that a PvP win happened
            newWinsPvP++
            newCurrentWinStreak++
            won = true
        }

        val newLongestWinStreak = maxOf(currentStats.longestWinStreak, newCurrentWinStreak)
        val newTotalDuration = currentStats.totalMatchDurationSeconds + durationSeconds

        val updatedStats = GameStatistics(
            id = 1,
            gamesPlayed = newGamesPlayed,
            winsPvP = newWinsPvP,
            winsAI = newWinsAI,
            lossesAI = newLossesAI,
            draws = newDraws,
            longestWinStreak = newLongestWinStreak,
            currentWinStreak = newCurrentWinStreak,
            totalMatchDurationSeconds = newTotalDuration
        )

        statisticsDao.saveStatistics(updatedStats)

        // Evaluate achievements
        val now = System.currentTimeMillis()
        
        if (won) {
            achievementsDao.unlockAchievement("first_win", now)
        }

        val totalWins = newWinsPvP + newWinsAI
        if (totalWins >= 10) {
            achievementsDao.unlockAchievement("10_wins", now)
        }
        if (totalWins >= 50) {
            achievementsDao.unlockAchievement("50_wins", now)
        }
        if (totalWins >= 100) {
            achievementsDao.unlockAchievement("100_wins", now)
        }

        if (newLongestWinStreak >= 5) {
            achievementsDao.unlockAchievement("unstoppable", now)
        }

        if (isAiMode) {
            // Check play AI 5 times
            val aiGames = newWinsAI + newLossesAI + newDraws // rough AI game estimation
            // Better to track exactly, or use total AI games. Let's unlock if total wins/losses/draws is high
            if (aiGames >= 5) {
                achievementsDao.unlockAchievement("ai_challenger", now)
            }

            // Perfect Player: Defeat AI and losses is 0
            if (newWinsAI >= 1 && newLossesAI == 0) {
                achievementsDao.unlockAchievement("perfect_player", now)
            }
        }

        // Master Strategist: Won in 5 or fewer moves (movesCount <= 5)
        if (won && movesCount <= 5) {
            achievementsDao.unlockAchievement("master_strategist", now)
        }
    }

    suspend fun resetStatistics() {
        statisticsDao.saveStatistics(GameStatistics(id = 1))
        achievementsDao.resetAchievements()
    }
}
