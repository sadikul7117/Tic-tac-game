package com.example.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface SettingsDao {
    @Query("SELECT * FROM settings WHERE id = 1")
    fun getSettingsFlow(): Flow<GameSettings?>

    @Query("SELECT * FROM settings WHERE id = 1")
    suspend fun getSettings(): GameSettings?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSettings(settings: GameSettings)
}

@Dao
interface StatisticsDao {
    @Query("SELECT * FROM statistics WHERE id = 1")
    fun getStatisticsFlow(): Flow<GameStatistics?>

    @Query("SELECT * FROM statistics WHERE id = 1")
    suspend fun getStatistics(): GameStatistics?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveStatistics(statistics: GameStatistics)
}

@Dao
interface AchievementsDao {
    @Query("SELECT * FROM achievements")
    fun getAchievementsFlow(): Flow<List<GameAchievement>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAchievements(achievements: List<GameAchievement>)

    @Update
    suspend fun updateAchievement(achievement: GameAchievement)

    @Query("UPDATE achievements SET isUnlocked = 1, unlockedTimestamp = :timestamp WHERE id = :id AND isUnlocked = 0")
    suspend fun unlockAchievement(id: String, timestamp: Long)

    @Query("UPDATE achievements SET isUnlocked = 0, unlockedTimestamp = NULL")
    suspend fun resetAchievements()
}
